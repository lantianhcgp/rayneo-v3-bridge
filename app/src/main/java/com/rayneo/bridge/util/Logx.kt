package com.rayneo.bridge.util

import android.content.Context
import java.io.File
import java.io.RandomAccessFile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** 按天日志文件 + 内存环形缓冲（lite-widget 风格，MCP log_tail 数据源） */
object Logx {
    @Volatile private var dir: File? = null
    private val ring = ArrayDeque<String>()
    private const val RING_MAX = 3000
    private val tf = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    private val df = SimpleDateFormat("yyyyMMdd", Locale.US)

    fun init(context: Context) {
        val d = File(context.filesDir, "logs")
        d.mkdirs()
        dir = d
        // 跨天清理：只保留今天
        d.listFiles()?.forEach { f ->
            val day = df.format(Date())
            if (f.name.startsWith("log") && !f.name.contains(day)) f.delete()
        }
    }

    @Synchronized
    fun i(tag: String, msg: String) = write("I", tag, msg)

    @Synchronized
    fun e(tag: String, msg: String, tr: Throwable? = null) =
        write("E", tag, msg + (tr?.let { " | ${it.javaClass.simpleName}: ${it.message}" } ?: ""))

    private fun write(lv: String, tag: String, msg: String) {
        val line = "${tf.format(Date())} $lv/$tag: $msg"
        synchronized(ring) {
            ring.addLast(line)
            while (ring.size > RING_MAX) ring.removeFirst()
        }
        val d = dir ?: return
        try {
            val f = File(d, "log${df.format(Date())}.txt")
            RandomAccessFile(f, "rw").use { it.seek(it.length()); it.write((line + "\n").toByteArray()) }
        } catch (_: Exception) {
        }
    }

    /** 从文件尾部读取最多 maxLines 行 */
    fun tail(maxLines: Int = 200): List<String> {
        val d = dir ?: return emptyList()
        val f = File(d, "log${df.format(Date())}.txt")
        if (!f.exists()) return synchronized(ring) { ring.takeLast(maxLines) }
        return try {
            RandomAccessFile(f, "r").use { r ->
                val len = r.length()
                val cap = 64 * 1024L
                val start = if (len > cap) len - cap else 0
                r.seek(start)
                val bytes = ByteArray((len - start).toInt())
                r.readFully(bytes)
                String(bytes).trimEnd('\n').split('\n').takeLast(maxLines)
            }
        } catch (e: Exception) {
            listOf("log_tail error: ${e.message}")
        }
    }

    fun clear() {
        synchronized(ring) { ring.clear() }
        dir?.listFiles()?.forEach { if (it.name.startsWith("log")) it.delete() }
    }
}
