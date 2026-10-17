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
            Logx.i("Ble", "rx char=${ch.uuid} len=${v.size} head=" + v.take(16).joinToString(" ") { "%02X".format(it) })
            rxListener?.invoke(v)
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
    fun disconnect() { gatt?.disconnect(); gatt?.close(); gatt = null; state = State.IDLE }
}
