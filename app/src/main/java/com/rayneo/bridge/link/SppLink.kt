package com.rayneo.bridge.link

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import com.rayneo.bridge.proto.Frame
import com.rayneo.bridge.util.Logx
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * SPP/RFCOMM 链路（V3 在官方 SPP 白名单内，DESIGN.md §2）
 * 复用系统已配对 bond：createInsecureRfcommSocketToServiceRecord 不触发重新配对。
 */
class SppLink(private val callbacks: Callbacks) {
    interface Callbacks {
        fun onState(state: State)
        fun onFrame(frame: Frame)
    }

    enum class State { IDLE, CONNECTING, CONNECTED, DISCONNECTED, FAILED }

    companion object {
        // 00001101 标准 SPP；官方 SDK 同时用 insecure 变体（逆向 strings 实证）
        val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    @Volatile private var state = State.IDLE
    @Volatile private var socket: BluetoothSocket? = null
    @Volatile private var reader: Thread? = null
    private val writerQueue = ConcurrentLinkedQueue<ByteArray>()
    @Volatile private var writerThread: Thread? = null
    private val msgNum = AtomicInteger(1)

    fun adapter(): BluetoothAdapter? =
        (com.rayneo.bridge.App.app.getSystemService(BluetoothManager::class.java))?.adapter

    fun currentState() = state

    private fun setState(s: State) { state = s; callbacks.onState(s) }

    @SuppressLint("MissingPermission")
    fun connect(mac: String): Boolean {
        if (state == State.CONNECTING || state == State.CONNECTED) return false
        val ad = adapter() ?: run { setState(State.FAILED); return false }
        val dev: BluetoothDevice = try { ad.getRemoteDevice(mac) } catch (e: Exception) {
            Logx.e("Spp", "bad mac $mac"); setState(State.FAILED); return false
        }
        setState(State.CONNECTING)
        Thread {
            try {
                ad.cancelDiscovery()
                // insecure RFCOMM：复用系统 bond，不弹配对
                val s = dev.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
                socket = s
                s.connect() // 阻塞；系统已配对则直连
                Logx.i("Spp", "connected to $mac")
                setState(State.CONNECTED)
                startReader(s.inputStream)
                startWriter(s.outputStream)
            } catch (e: Exception) {
                Logx.e("Spp", "connect failed", e)
                closeQuietly()
                setState(State.FAILED)
            }
        }.start()
        return true
    }

    private fun startReader(input: InputStream) {
        reader = Thread({
            val buf = ByteArray(8192)
            val acc = java.io.ByteArrayOutputStream()
            try {
                while (true) {
                    val n = input.read(buf)
                    if (n < 0) break
                    acc.write(buf, 0, n)
                    // 粘包处理：循环取完整帧
                    var bytes = acc.toByteArray()
                    var progress = true
                    while (progress && bytes.size >= Frame.MIN_PACKET_SIZE) {
                        val head = ((bytes[0].toInt() and 0xFF) shl 8) or (bytes[1].toInt() and 0xFF)
                        if (head != Frame.HEAD_REQUEST && head != Frame.HEAD_RESPONSE) {
                            Logx.e("Spp", "resync: drop byte %02X".format(bytes[0]))
                            bytes = bytes.copyOfRange(1, bytes.size); continue
                        }
                        if (bytes.size < Frame.HEADER_SIZE) { progress = false; continue }
                        val len = ((bytes[3].toInt() and 0xFF) shl 8) or (bytes[4].toInt() and 0xFF)
                        val total = Frame.HEADER_SIZE + len + 2
                        if (len > Frame.MAX_PAYLOAD) { Logx.e("Spp", "bad len $len, resync"); bytes = bytes.copyOfRange(2, bytes.size); continue }
                        if (bytes.size < total) { progress = false; continue }
                        val frameBytes = bytes.copyOfRange(0, total)
                        bytes = bytes.copyOfRange(total, bytes.size)
                        val f = Frame.decode(frameBytes)
                        if (f != null) callbacks.onFrame(f)
                    }
                    acc.reset(); acc.write(bytes)
                }
                Logx.i("Spp", "reader EOF")
            } catch (e: Exception) {
                Logx.e("Spp", "reader error", e)
            } finally {
                closeQuietly(); setState(State.DISCONNECTED)
            }
        }, "spp-reader").also { it.isDaemon = true; it.start() }
    }

    private fun startWriter(output: OutputStream) {
        writerThread = Thread({
            try {
                while (state == State.CONNECTED) {
                    val pkt = writerQueue.poll()
                    if (pkt == null) { Thread.sleep(10); continue }
                    output.write(pkt); output.flush()
                    Logx.i("Spp", "tx ${pkt.size}B head=%02X%02X biz=%d".format(pkt[0], pkt[1], pkt[8].toInt() and 0xFF))
                    Thread.sleep(5)
                }
            } catch (e: Exception) {
                Logx.e("Spp", "writer error", e)
            }
        }, "spp-writer").also { it.isDaemon = true; it.start() }
    }

    fun send(frame: Frame): Boolean {
        if (state != State.CONNECTED) { Logx.e("Spp", "send while $state"); return false }
        writerQueue.offer(Frame.encode(frame))
        return true
    }

    fun nextMsgNum(): Int = msgNum.getAndIncrement() and 0xFFFF

    fun disconnect() { closeQuietly(); setState(State.DISCONNECTED) }

    private fun closeQuietly() {
        try { socket?.close() } catch (_: Exception) {}
        socket = null
    }
}
