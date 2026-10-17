package com.rayneo.bridge.assistant

import com.rayneo.bridge.proto.Frame
import com.rayneo.bridge.util.Logx
import java.util.ArrayDeque

/**
 * 语音助手通道（BusinessID=14）处理（逆向 DESIGN.md §4）
 *
 * 官方消息类型：1=唤醒 3=ASR音频 8=退出 35=VQA图 33=技能回调（眼镜→手机）
 *               2=录音开关 4=VAD 5=ASR文本 6=TTS状态 7=退出 9=会话状态 32=技能协议（手机→眼镜）
 *
 * v0.1 策略：全量记录消息类型与 payload 十六进制（真机对照官方 App 抓包校准字段布局）；
 *           send_text() 走 type5（ASR文本）+ SpeechTransportPayload 风格 payload，验证下行链路。
 */
class AssistantChannel {
    data class Event(val ts: Long, val dir: String, val msgType: Int, val size: Int, val hexHead: String)

    private val events = ArrayDeque<Event>()
    private val MAX = 500

    fun onFrame(frame: Frame) {
        // payload 首字节通常为 assistant 消息类型（待真机确认；也可能在 TLV 里）
        val msgType = if (frame.payload.isNotEmpty()) frame.payload[0].toInt() and 0xFF else -1
        val hex = frame.payload.take(24).joinToString(" ") { "%02X".format(it) }
        synchronized(events) {
            events.addLast(Event(System.currentTimeMillis(), "rx", msgType, frame.payload.size, hex))
            while (events.size > MAX) events.removeFirst()
        }
        Logx.i("Assistant", "rx msgType=$msgType len=${frame.payload.size} head=[$hex]")
        when (msgType) {
            1 -> Logx.i("Assistant", "眼镜唤醒请求")
            3 -> Logx.i("Assistant", "收到ASR音频流 len=${frame.payload.size}（TODO: 接自有ASR）")
            8 -> Logx.i("Assistant", "眼镜退出请求")
            else -> {}
        }
    }

    /** 下行：ASR 文本（type5）。payload 布局待真机校准 */
    fun sendAsrText(text: String): Boolean {
        val body = byteArrayOf(5.toByte()) + text.toByteArray(Charsets.UTF_8)
        return send(Frame.BIZ_VOICE_ASSISTANT, body)
    }

    /** 下行：通用助手消息（msgType + raw body） */
    fun send(bizPayloadType: Int, body: ByteArray): Boolean {
        val payload = byteArrayOf(bizPayloadType.toByte()) + body
        val frame = Frame(
            isResponse = false, packetType = 0, msgNum = com.rayneo.bridge.link.LinkManager.spp.nextMsgNum(),
            address = 0, businessId = Frame.BIZ_VOICE_ASSISTANT, payload = payload
        )
        Logx.i("Assistant", "tx msgType=$bizPayloadType len=${payload.size}")
        return com.rayneo.bridge.link.LinkManager.spp.send(frame)
    }

    fun recentEvents(): List<Map<String, Any?>> = synchronized(events) {
        events.toList().takeLast(50).map {
            mapOf("ts" to it.ts, "dir" to it.dir, "msgType" to it.msgType, "size" to it.size, "head" to it.hexHead)
        }
    }
}
