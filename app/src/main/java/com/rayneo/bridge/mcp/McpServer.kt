package com.rayneo.bridge.mcp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import com.rayneo.bridge.App
import com.rayneo.bridge.link.Config
import com.rayneo.bridge.link.LinkManager
import com.rayneo.bridge.util.Json
import com.rayneo.bridge.util.Logx
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

/**
 * LAN MCP server（lite-widget 同款风格）：POST /mcp，JSON-RPC 2.0，Bearer token。
 * 局域网 0.0.0.0 监听，本机 AI 连 127.0.0.1。
 * 工具即 App 能力出口：连接管理 / 发文本给眼镜 / 助手消息观测 / 日志 / 配置。
 */
class McpServer {
    @Volatile private var serverSocket: ServerSocket? = null
    @Volatile private var running = false
    // 注意：不能在构造期读 Config（companion 初始化早于 Application.onCreate，prefs 未就绪会秒崩）
    var port: Int = 0
        private set

    fun start() {
        if (running) return
        Thread {
            try {
                val ss = ServerSocket(Config.mcpPort, 50, InetAddress.getByName("0.0.0.0"))
                serverSocket = ss; running = true; port = ss.localPort
                Logx.i("Mcp", "listening on 0.0.0.0:$port")
                while (running) {
                    val sock = ss.accept()
                    Thread { handle(sock) }.start()
                }
            } catch (e: Exception) {
                Logx.e("Mcp", "server error", e); running = false
            }
        }.also { it.isDaemon = true; it.start() }
    }

    fun stop() { running = false; try { serverSocket?.close() } catch (_: Exception) {} }

    private fun handle(sock: Socket) {
        sock.use { s ->
            s.soTimeout = 15000
            try {
                val input = s.getInputStream()
                val reader = BufferedReader(InputStreamReader(input, Charsets.UTF_8))
                // 解析请求行 + 头 + body
                val reqLine = reader.readLine() ?: return
                val parts = reqLine.split(" ")
                if (parts.size < 2) return respond(s, 400, """{"error":"bad request"}""")
                val headers = HashMap<String, String>()
                while (true) {
                    val line = reader.readLine() ?: break
                    if (line.isEmpty()) break
                    val idx = line.indexOf(':')
                    if (idx > 0) headers[line.substring(0, idx).trim().lowercase()] = line.substring(idx + 1).trim()
                }
                val clen = headers["content-length"]?.toIntOrNull() ?: 0
                val bodyChars = CharArray(clen)
                var read = 0
                while (read < clen) {
                    val n = reader.read(bodyChars, read, clen - read)
                    if (n < 0) break
                    read += n
                }
                // token 校验（token 为空=未初始化则放行并打印，方便首次配对）
                val auth = headers["authorization"] ?: ""
                val token = Config.mcpToken
                if (token.isNotEmpty() && !auth.startsWith("Bearer $token")) {
                    Logx.i("Mcp", "WARN 401 unauthorized")
                    return respond(s, 401, """{"error":"unauthorized"}""")
                }
                if (parts[1] != "/mcp") return respond(s, 404, """{"error":"not found"}""")
                val resp = dispatch(String(bodyChars, 0, read))
                respond(s, 200, resp)
            } catch (e: Exception) {
                Logx.e("Mcp", "conn error", e)
                try { respond(s, 500, """{"error":"${e.message?.replace("\"", "'")}"}""") } catch (_: Exception) {}
            }
        }
    }

    private fun respond(s: Socket, code: Int, body: String) {
        val os: OutputStream = s.getOutputStream()
        val bytes = body.toByteArray(Charsets.UTF_8)
        val status = if (code == 200) "OK" else if (code == 401) "Unauthorized" else if (code == 404) "Not Found" else "Error"
        val head = "HTTP/1.1 $code $status\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
        os.write(head.toByteArray()); os.write(bytes); os.flush()
    }

    private fun dispatch(body: String): String {
        val req = Json.parse(body) as? Map<*, *> ?: return rpcError(null, -32700, "parse error")
        val id = req["id"]
        val method = req["method"] as? String ?: return rpcError(id, -32600, "no method")
        val params = req["params"] as? Map<*, *>
        return when (method) {
            "initialize" -> rpcResult(id, mapOf(
                "protocolVersion" to "2024-11-05",
                "capabilities" to mapOf("tools" to mapOf<String, Any>()),
                "serverInfo" to mapOf("name" to "rayneo-bridge", "version" to App.version)
            ))
            "tools/list" -> rpcResult(id, mapOf("tools" to TOOL_DEFS))
            "tools/call" -> {
                val name = params?.get("name") as? String ?: return rpcError(id, -32602, "no tool name")
                val args = params["arguments"] as? Map<*, *> ?: emptyMap<String, Any?>()
                try {
                    val out = callTool(name, args)
                    rpcResult(id, listOf(mapOf("type" to "text", "text" to out)))
                } catch (e: Exception) {
                    Logx.e("Mcp", "tool $name failed", e)
                    rpcResult(id, listOf(mapOf("type" to "text", "text" to """{"error":"${e.message}"}""")))
                }
            }
            "ping" -> rpcResult(id, mapOf("pong" to true))
            else -> rpcError(id, -32601, "unknown method $method")
        }
    }

    private fun callTool(name: String, args: Map<*, *>): String = when (name) {
        "status" -> Json.write(mapOf(
            "link" to LinkManager.status(),
            "mcp" to mapOf("port" to port, "tokenSet" to Config.mcpToken.isNotEmpty()),
            "llm" to mapOf("endpoint" to Config.llmEndpoint, "model" to Config.llmModel, "keySet" to Config.llmKey.isNotEmpty()),
            "version" to App.version
        ))
        "devices" -> {
            val ad = (App.app.getSystemService(BluetoothManager::class.java))?.adapter
            val list = ad?.bondedDevices?.map {
                mapOf("name" to it.name, "mac" to it.address)
            } ?: emptyList()
            Json.write(mapOf("bonded" to list))
        }
        "connect" -> {
            val mac = args["mac"] as? String
            val ok = LinkManager.connect(mac)
            Json.write(mapOf("connecting" to ok, "target" to (mac ?: LinkManager.status()["mac"])))
        }
        "disconnect" -> { LinkManager.disconnect(); LinkManager.ble.disconnect(); Json.write(mapOf("ok" to true)) }
        "ble_send" -> {
            val hex = args["hex"] as? String ?: ""
            if (hex.isEmpty()) Json.write(mapOf("error" to "no hex")) else Json.write(mapOf("sent" to LinkManager.ble.sendHex(hex)))
        }
        "ble_send_json" -> {
            val j = args["json"] as? String ?: ""
            if (j.isEmpty()) Json.write(mapOf("error" to "no json")) else Json.write(mapOf("sent" to LinkManager.ble.sendJson(j)))
        }
        "ble_messages" -> Json.write(mapOf("messages" to LinkManager.ble.recentMessages()))
        "connect_ble" -> {
            val mac = args["mac"] as? String
            val ok = LinkManager.ble.connect(mac)
            Json.write(mapOf("connecting" to ok, "target" to (mac ?: LinkManager.status()["mac"])))
        }
        "assistant_send" -> {
            val type = (args["msgType"] as? Number)?.toInt() ?: 5
            val text = args["text"] as? String ?: ""
            val ok = if (type == 5) LinkManager.assistant.sendAsrText(text)
                     else LinkManager.assistant.send(type, text.toByteArray(Charsets.UTF_8))
            Json.write(mapOf("sent" to ok, "msgType" to type))
        }
        "assistant_events" -> Json.write(mapOf("events" to LinkManager.assistant.recentEvents()))
        "log_tail" -> {
            val n = (args["lines"] as? Number)?.toInt() ?: 200
            Json.write(mapOf("lines" to Logx.tail(n.coerceIn(1, 2000))))
        }
        "log_clear" -> { Logx.clear(); Json.write(mapOf("ok" to true)) }
        "config_get" -> Json.write(mapOf(
            "target_mac" to Config.targetMac, "llm_endpoint" to Config.llmEndpoint,
            "llm_model" to Config.llmModel, "llm_key_set" to Config.llmKey.isNotEmpty(),
            "mcp_port" to Config.mcpPort, "mcp_token_set" to Config.mcpToken.isNotEmpty()
        ))
        "config_set" -> {
            (args["target_mac"] as? String)?.let { Config.targetMac = it }
            (args["llm_endpoint"] as? String)?.let { Config.llmEndpoint = it }
            (args["llm_key"] as? String)?.let { Config.llmKey = it }
            (args["llm_model"] as? String)?.let { Config.llmModel = it }
            (args["mcp_token"] as? String)?.let { Config.mcpToken = it }
            Json.write(mapOf("ok" to true, "note" to "mcp_port 需重启 App 生效"))
        }
        else -> Json.write(mapOf("error" to "unknown tool $name"))
    }

    private fun rpcResult(id: Any?, result: Any?) =
        Json.write(mapOf("jsonrpc" to "2.0", "id" to id, "result" to result))
    private fun rpcError(id: Any?, code: Int, msg: String) =
        Json.write(mapOf("jsonrpc" to "2.0", "id" to id, "error" to mapOf("code" to code, "message" to msg)))

    companion object {
        private val TOOL_DEFS = listOf(
            tool("status", "App/链路/MCP/LLM 配置总状态", mapOf()),
            tool("devices", "列出系统已配对蓝牙设备（找眼镜 MAC）", mapOf()),
            tool("connect", "连接眼镜 SPP", mapOf("mac" to prop("string", "目标 MAC，省略用已存配置"))),
            tool("disconnect", "断开眼镜", mapOf()),
            tool("connect_ble", "连接眼镜 BLE GATT（b81d 控制链）", mapOf("mac" to prop("string", "目标 MAC，省略用配置"))),
            tool("ble_send", "向眼镜 BLE write 特征值发十六进制帧", mapOf("hex" to prop("string", "hex 载荷，如 AB 55 00 00"))),
            tool("ble_send_json", "向眼镜发 JSON 指令（自动分片 01..00）", mapOf("json" to prop("string", "完整 JSON，如 {"id":"1","uri":"ble.request.xxx"}"))),
            tool("ble_messages", "查看眼镜推来的完整 JSON 消息（重组后）", mapOf()),
            tool("assistant_send", "向眼镜发助手消息（msgType=5 为 ASR 文本，会显示/朗读）",
                mapOf("msgType" to prop("integer", "助手消息类型"), "text" to prop("string", "文本内容"))),
            tool("assistant_events", "最近50条助手通道消息观测（类型/方向/长度/头部hex）", mapOf()),
            tool("log_tail", "App 日志尾部", mapOf("lines" to prop("integer", "行数，默认200"))),
            tool("log_clear", "清空日志", mapOf()),
            tool("config_get", "读配置", mapOf()),
            tool("config_set", "写配置（target_mac/llm_endpoint/llm_key/llm_model/mcp_token）", mapOf(
                "target_mac" to prop("string", "眼镜MAC"), "llm_endpoint" to prop("string", "OpenAI兼容base_url"),
                "llm_key" to prop("string", "API key"), "llm_model" to prop("string", "模型名"),
                "mcp_token" to prop("string", "MCP访问token")
            ))
        )

        private fun prop(type: String, desc: String) = mapOf("type" to type, "description" to desc)
        private fun tool(name: String, desc: String, props: Map<String, Any>) = mapOf(
            "name" to name, "description" to desc,
            "inputSchema" to mapOf("type" to "object", "properties" to props)
        )
    }
}
