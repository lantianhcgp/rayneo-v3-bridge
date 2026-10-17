package com.rayneo.bridge.util

/** 极简 JSON 解析/生成（零依赖，MCP 用） */
object Json {
    fun parse(s: String): Any? = Parser(s).parseValue()

    fun write(v: Any?): String = StringBuilder().also { write(v, it) }.toString()

    private fun write(v: Any?, sb: StringBuilder) {
        when (v) {
            null -> sb.append("null")
            is String -> {
                sb.append('"')
                for (c in v) when (c) {
                    '"' -> sb.append("\\\"")
                    '\\' -> sb.append("\\\\")
                    '\n' -> sb.append("\\n")
                    '\r' -> sb.append("\\r")
                    '\t' -> sb.append("\\t")
                    else -> if (c < ' ') sb.append("\\u%04x".format(c.code)) else sb.append(c)
                }
                sb.append('"')
            }
            is Number, is Boolean -> sb.append(v.toString())
            is Map<*, *> -> {
                sb.append('{')
                v.entries.forEachIndexed { i, (k, mv) ->
                    if (i > 0) sb.append(',')
                    write(k.toString(), sb); sb.append(':'); write(mv, sb)
                }
                sb.append('}')
            }
            is List<*> -> {
                sb.append('[')
                v.forEachIndexed { i, mv -> if (i > 0) sb.append(','); write(mv, sb) }
                sb.append(']')
            }
            else -> write(v.toString(), sb)
        }
    }

    private class Parser(val s: String) {
        var i = 0
        fun parseValue(): Any? {
            skipWs()
            if (i >= s.length) return null
            return when (s[i]) {
                '{' -> parseObj()
                '[' -> parseArr()
                '"' -> parseStr()
                't' -> { i += 4; true }
                'f' -> { i += 5; false }
                'n' -> { i += 4; null }
                else -> parseNum()
            }
        }
        fun parseObj(): Map<String, Any?> {
            val m = LinkedHashMap<String, Any?>(); i++ // {
            skipWs()
            if (s[i] == '}') { i++; return m }
            while (true) {
                skipWs(); val k = parseStr(); skipWs(); i++ // :
                m[k] = parseValue(); skipWs()
                if (s[i] == ',') { i++; continue }
                if (s[i] == '}') { i++; return m }
                throw IllegalArgumentException("bad obj at $i")
            }
        }
        fun parseArr(): List<Any?> {
            val l = ArrayList<Any?>(); i++ // [
            skipWs()
            if (s[i] == ']') { i++; return l }
            while (true) {
                l.add(parseValue()); skipWs()
                if (s[i] == ',') { i++; continue }
                if (s[i] == ']') { i++; return l }
                throw IllegalArgumentException("bad arr at $i")
            }
        }
        fun parseStr(): String {
            val sb = StringBuilder(); i++ // "
            while (s[i] != '"') {
                if (s[i] == '\\') {
                    i++
                    when (s[i]) {
                        'n' -> sb.append('\n'); 't' -> sb.append('\t'); 'r' -> sb.append('\r')
                        '"' -> sb.append('"'); '\\' -> sb.append('\\'); '/' -> sb.append('/')
                        'u' -> { sb.append(s.substring(i + 1, i + 5).toInt(16).toChar()); i += 4 }
                        else -> {}
                    }
                } else sb.append(s[i])
                i++
            }
            i++; return sb.toString()
        }
        fun parseNum(): Number {
            val st = i
            while (i < s.length && (s[i].isDigit() || s[i] in "-+.eE")) i++
            val t = s.substring(st, i)
            return if (t.contains('.') || t.contains('e') || t.contains('E')) t.toDouble() else t.toLong()
        }
        fun skipWs() { while (i < s.length && s[i].isWhitespace()) i++ }
    }
}
