package com.rayneo.bridge

import android.app.Application
import android.content.Context
import com.rayneo.bridge.assistant.AssistantChannel
import com.rayneo.bridge.link.Config
import com.rayneo.bridge.link.LinkManager
import com.rayneo.bridge.mcp.McpServer
import com.rayneo.bridge.util.Logx
import java.util.UUID

class App : Application() {
    companion object {
        lateinit var app: App
        const val version = "0.1.4"
        val mcp = McpServer()
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        Logx.init(this)
        Config.prefs = getSharedPreferences("bridge", Context.MODE_PRIVATE)
        if (Config.mcpToken.isEmpty()) {
            Config.mcpToken = UUID.randomUUID().toString().replace("-", "").substring(0, 24)
            Logx.i("App", "generated mcp token: ${Config.mcpToken}")
        }
        LinkManager.assistant = AssistantChannel()
        mcp.start()
        Logx.i("App", "RayNeo Bridge $version started, mcp port=${mcp.port}")
    }
}
