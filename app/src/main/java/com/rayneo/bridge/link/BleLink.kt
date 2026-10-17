package com.rayneo.bridge.link

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.rayneo.bridge.App
import com.rayneo.bridge.util.Logx
import java.util.UUID

class BleLink {
    enum class State { IDLE, CONNECTING, DISCOVERING, READY, FAILED, DISCONNECTED }
    @Volatile var state = State.IDLE
    @Volatile private var gatt: BluetoothGatt? = null
    var rxListener: ((ByteArray) -> Unit)? = null
    private val buf = java.io.ByteArrayOutputStream()
    private val msgs = ArrayDeque<String>()

    fun recentMessages(): List<String> = synchronized(msgs) { msgs.takeLast(50) }

    companion object {
        val SVC = UUID.fromString("0000b81d-0000-1000-8000-00805f9b34fb")
        val CHAR_WRITE = UUID.fromString("ea8b70d5-2bd3-49ab-9c31-9c38b2c3c4f9")
        val CHAR_RECEIVE = UUID.fromString("7db3e235-3608-41f3-a03c-955fcbd2ea4b")
        val CHAR_HEART = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb")
        val CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    private val cb = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> { state = State.DISCOVERING; Logx.i("Ble", "GATT connected, discovering"); g.discoverServices() }
                BluetoothProfile.STATE_DISCONNECTED -> { state = State.DISCONNECTED; Logx.i("Ble", "GATT disconnected status=$status") }
            }
        }
        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
            val svc = g.getService(SVC)
            if (svc == null) { Logx.e("Ble", "service b81d not found: " + g.services.joinToString { it.uuid.toString() }); state = State.FAILED; return }
            gatt = g
            for (u in listOf(CHAR_RECEIVE, CHAR_HEART)) {
                svc.getCharacteristic(u)?.let { ch ->
                    g.setCharacteristicNotification(ch, true)
                    ch.getDescriptor(CCCD)?.let { d -> d.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE; g.writeDescriptor(d) }
                }
            }
            state = State.READY
            Logx.i("Ble", "READY write=" + (svc.getCharacteristic(CHAR_WRITE) != null) + " receive=" + (svc.getCharacteristic(CHAR_RECEIVE) != null) + " heart=" + (svc.getCharacteristic(CHAR_HEART) != null))
        }
        override fun onCharacteristicChanged(g: BluetoothGatt, ch: BluetoothGattCharacteristic) {
            val v = ch.value ?: return
            if (ch.uuid != CHAR_RECEIVE && ch.uuid != CHAR_HEART) { Logx.i("Ble", "rx other char=${ch.uuid} len=${v.size}"); return }
            if (v.isEmpty()) return
            val seq = v[0].toInt() and 0xFF
            if (seq == 1) buf.reset()
            buf.write(v, 1, v.size - 1)
            Logx.i("Ble", "rx seq=$seq len=${v.size}")
            rxListener?.invoke(v)
            if (seq == 0) {
                val msg = String(buf.toByteArray(), Charsets.UTF_8)
                buf.reset()
                synchronized(msgs) { msgs.addLast(msg); while (msgs.size > 100) msgs.removeFirst() }
                Logx.i("Ble", "json<- ${msg.take(400)}")
            }
        }
        override fun onCharacteristicWrite(g: BluetoothGatt, ch: BluetoothGattCharacteristic, status: Int) {
            if (status != 0) Logx.e("Ble", "write status=$status")
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(mac: String?): Boolean {
        val target = mac ?: Config.targetMac
        if (target.isEmpty()) return false
        Config.targetMac = target
        val ad = (App.app.getSystemService(BluetoothManager::class.java))?.adapter ?: return false
        val dev: BluetoothDevice = ad.getRemoteDevice(target)
        state = State.CONNECTING
        gatt = dev.connectGatt(App.app as Context, false, cb, BluetoothDevice.TRANSPORT_LE)
        Logx.i("Ble", "connectGatt -> $target")
        return true
    }

    @SuppressLint("MissingPermission")
    fun sendHex(hex: String): Boolean {
        val g = gatt ?: return false
        val ch = g.getService(SVC)?.getCharacteristic(CHAR_WRITE) ?: return false
        val bytes = hex.replace(" ", "").chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        ch.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ch.value = bytes
        val ok = g.writeCharacteristic(ch)
        Logx.i("Ble", "tx ${bytes.size}B hex=$hex ok=$ok")
        return ok
    }

    /** 发送 JSON 消息：分19字节片，seq 01..0N,00(尾)，写 CHAR_WRITE */
    @SuppressLint("MissingPermission")
    fun sendJson(json: String): Boolean {
        val g = gatt ?: return false
        val ch = g.getService(SVC)?.getCharacteristic(CHAR_WRITE) ?: return false
        val parts = json.toByteArray(Charsets.UTF_8).toList().chunked(19)
        ch.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        parts.forEachIndexed { i, part ->
            val seq = if (i == parts.lastIndex) 0 else i + 1
            val bytes = byteArrayOf(seq.toByte()) + part.toByteArray()
            ch.value = bytes
            if (!g.writeCharacteristic(ch)) { Logx.e("Ble", "chunk $i write failed"); return false }
            Thread.sleep(40)
        }
        Logx.i("Ble", "json-> (${parts.size}ch) ${json.take(300)}")
        return true
    }

    @SuppressLint("MissingPermission")
    fun disconnect() { gatt?.disconnect(); gatt?.close(); gatt = null; state = State.IDLE }
}
