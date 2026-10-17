package com.rayneo.bridge

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder

/** 前台服务：保活 MCP + SPP 连接（connectedDevice 类型） */
class BridgeService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, buildNotification())
        return START_STICKY
    }

    private fun buildNotification(): Notification {
        val nm = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(NotificationChannel("bridge", "RayNeo Bridge", NotificationManager.IMPORTANCE_LOW))
        }
        return Notification.Builder(this, "bridge")
            .setContentTitle("RayNeo Bridge 运行中")
            .setContentText("MCP :${App.mcp.port} · ${com.rayneo.bridge.link.LinkManager.status()["spp"]}")
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .build()
    }
}
