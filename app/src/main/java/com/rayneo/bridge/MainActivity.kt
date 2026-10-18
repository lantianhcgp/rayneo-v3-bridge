package com.rayneo.bridge

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.app.Activity
import com.rayneo.bridge.link.LinkManager
import com.rayneo.bridge.util.Logx

class MainActivity : Activity() {
    private lateinit var statusView: TextView
    private lateinit var mcpView: TextView
    private lateinit var logView: TextView
    private val refresh = object : Runnable {
        override fun run() {
            update()
            logView.postDelayed(this, 2000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 4242) {
            if (resultCode == RESULT_OK) {
                Logx.i("Vpn", "VPN 授权成功，启动观测器")
                startService(android.content.Intent(this, com.rayneo.bridge.vpn.DnsObserverService::class.java))
            } else {
                Logx.i("Vpn", "VPN 授权被拒绝")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.getBooleanExtra("vpn_consent", false) == true) {
            val prep = android.net.VpnService.prepare(this)
            if (prep != null) startActivityForResult(prep, 4242)
            else startService(android.content.Intent(this, com.rayneo.bridge.vpn.DnsObserverService::class.java))
        }
        val dp = resources.displayMetrics.density
        fun px(v: Int) = (v * dp).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0B0C10"))
            setPadding(px(20), px(40), px(20), px(20))
        }
        statusView = TextView(this).apply {
            setTextColor(Color.WHITE); textSize = 18f; text = "RayNeo Bridge v${App.version}"
        }
        mcpView = TextView(this).apply {
            setTextColor(Color.parseColor("#7C6CFF")); textSize = 13f
        }
        val connectBtn = Button(this).apply {
            text = "连接已配对眼镜"
            setOnClickListener {
                val mac = com.rayneo.bridge.link.Config.targetMac.ifEmpty {
                    // 默认取第一个已配对设备（后续 MCP devices 精确选）
                    (getSystemService(BluetoothManager::class.java)?.adapter?.bondedDevices?.firstOrNull()?.address) ?: ""
                }
                if (mac.isEmpty()) { statusView.text = "无已配对设备，请先在系统设置配对眼镜"; return@setOnClickListener }
                LinkManager.connect(mac)
            }
        }
        val permBtn = Button(this).apply {
            text = "授予蓝牙权限"
            setOnClickListener { requestBt() }
        }
        logView = TextView(this).apply {
            setTextColor(Color.parseColor("#9AA0AE")); textSize = 11f
            typeface = android.graphics.Typeface.MONOSPACE
        }
        val scroll = ScrollView(this).apply { addView(logView) }

        root.addView(statusView)
        root.addView(mcpView)
        root.addView(connectBtn)
        root.addView(permBtn)
        root.addView(scroll, LinearLayout.LayoutParams(0, 0, 1f).apply { topMargin = px(12) })
        setContentView(root)

        LinkManager.stateListener = { s -> runOnUiThread { statusView.text = "链路: $s" } }
        requestBt()
        startForegroundService(android.content.Intent(this, BridgeService::class.java))
        update()
        refresh.let { logView.postDelayed(it, 2000) }
    }

    private fun requestBt() {
        val need = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= 31) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                need += Manifest.permission.BLUETOOTH_CONNECT
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
                need += Manifest.permission.BLUETOOTH_SCAN
        }
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                need += Manifest.permission.POST_NOTIFICATIONS
        }
        if (need.isNotEmpty()) requestPermissions(need.toTypedArray(), 1)
    }

    private fun update() {
        val ip = localIp()
        mcpView.text = "MCP: http://$ip:${App.mcp.port}/mcp\ntoken: ${com.rayneo.bridge.link.Config.mcpToken}\n(本机调试用 127.0.0.1)"
        if (statusView.text.toString().startsWith("RayNeo")) {
            statusView.text = "链路: ${LinkManager.spp.currentState()}"
        }
        logView.text = Logx.tail(60).joinToString("\n")
    }

    private fun localIp(): String {
        try {
            java.net.NetworkInterface.getNetworkInterfaces().toList().forEach { n ->
                n.inetAddresses.toList().firstOrNull {
                    !it.isLoopbackAddress && it is java.net.Inet4Address
                }?.let { return it.hostAddress ?: "?" }
            }
        } catch (_: Exception) {}
        return "127.0.0.1"
    }

    override fun onDestroy() {
        super.onDestroy()
        logView.removeCallbacks(refresh)
    }
}
