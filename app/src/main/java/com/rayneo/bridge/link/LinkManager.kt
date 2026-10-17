package com.rayneo.bridge.link

import com.rayneo.bridge.assistant.AssistantChannel
import com.rayneo.bridge.proto.Frame
import com.rayneo.bridge.util.Logx

/** 链路状态中枢：持有 SPP、按业务号分发收到的帧 */
object LinkManager {
    lateinit var assistant: AssistantChannel

    val spp = SppLink(object : SppLink.Callbacks {
        override fun onState(state: SppLink.State) {
            Logx.i("Link", "spp state=$state")
            stateListener?.invoke(state.name)
        }
        override fun onFrame(frame: Frame) {
            Logx.i("Link", "rx biz=${frame.businessId} type=${frame.packetType} msg=${frame.msgNum} len=${frame.payload.size}")
            when (frame.businessId) {
                Frame.BIZ_VOICE_ASSISTANT -> assistant.onFrame(frame)
                else -> {} // 其余业务先只记日志
            }
        }
    })

    @Volatile var stateListener: ((String) -> Unit)? = null

    fun status(): Map<String, Any?> = mapOf(
        "spp" to spp.currentState().name,
        "mac" to Config.targetMac
    )

    fun connect(mac: String? = null): Boolean {
        val target = mac ?: Config.targetMac
        if (target.isEmpty()) return false
        Config.targetMac = target
        return spp.connect(target)
    }

    fun disconnect() { spp.disconnect() }
}

/** 简单 K-V 配置（SharedPreferences 由 App 初始化注入） */
object Config {
    lateinit var prefs: android.content.SharedPreferences
    var targetMac: String
        get() = prefs.getString("target_mac", "") ?: ""
        set(v) = prefs.edit().putString("target_mac", v).apply()
    var llmEndpoint: String
        get() = prefs.getString("llm_endpoint", "") ?: ""
        set(v) = prefs.edit().putString("llm_endpoint", v).apply()
    var llmKey: String
        get() = prefs.getString("llm_key", "") ?: ""
        set(v) = prefs.edit().putString("llm_key", v).apply()
    var llmModel: String
        get() = prefs.getString("llm_model", "qwen-plus") ?: "qwen-plus"
        set(v) = prefs.edit().putString("llm_model", v).apply()
    var mcpToken: String
        get() = prefs.getString("mcp_token", "") ?: ""
        set(v) = prefs.edit().putString("mcp_token", v).apply()
    var mcpPort: Int
        get() = prefs.getInt("mcp_port", 8766) ?: 8766
        set(v) = prefs.edit().putInt("mcp_port", v).apply()
}
