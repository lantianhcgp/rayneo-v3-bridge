package com.rayneo.bridge.vpn

import android.app.Service
import android.content.Intent
import android.net.VpnService
import android.os.IBinder
import com.rayneo.bridge.util.Logx
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.DatagramSocket
import java.net.DatagramPacket
import java.net.InetSocketAddress
import java.util.ArrayDeque

/**
 * DNS 观测器（v0.3）：TUN 只接管 DNS(UDP/53)，记录域名后转发真实 DNS 并回填。
 * 其他流量不进隧道（未路由 = 原生直连），零破坏。用途：定位官方 App/眼镜的 AI 云端域名。
 */
class DnsObserverService : VpnService() {

    companion object {
        @Volatile var running = false
        private val events = ArrayDeque<String>()   // "ts domain"
        private val counts = LinkedHashMap<String, Int>()
        fun eventsTail(n: Int): List<String> = synchronized(events) { events.toList().takeLast(n) }
        fun hostCounts(): Map<String, Int> = synchronized(counts) { counts.toMap() }
        private fun record(domain: String) {
            synchronized(events) {
                events.addLast("${System.currentTimeMillis() / 1000} $domain")
                while (events.size > 500) events.removeFirst()
                counts[domain] = (counts[domain] ?: 0) + 1
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!running) startDns()
        return START_STICKY
    }

    private fun startDns() {
        val pfd = Builder()
            .setSession("RayNeoObserver")
            .addAddress("10.111.222.1", 24)
            .addRoute("10.111.222.0", 24)      // 只路由本隧道 DNS 地址
            .addDnsServer("10.111.222.53")
            .establish() ?: run { Logx.e("Vpn", "establish failed"); return }
        running = true
        Thread({ pump(pfd.fileDescriptor) }, "dns-observer").apply { isDaemon = true; start() }
        Logx.i("Vpn", "DNS observer started")
    }

    private fun pump(fd: java.io.FileDescriptor) {
        val inp = FileInputStream(fd)
        val out = FileOutputStream(fd)
        val buf = ByteArray(4096)
        val udp = DatagramSocket()
        try { protect(udp) } catch (e: Exception) { Logx.e("Vpn", "protect fail: $e") }
        while (running) {
            val n = try { inp.read(buf) } catch (e: Exception) { -1 }
            if (n <= 0) break
            // IPv4 + UDP/53 校验
            if (n < 28 || buf[0].toInt() shr 4 != 4) continue
            val proto = buf[9].toInt()
            if (proto != 17) continue
            val ihl = (buf[0].toInt() and 0xF) * 4
            val dstPort = ((buf[ihl + 2].toInt() and 0xFF) shl 8) or (buf[ihl + 3].toInt() and 0xFF)
            if (dstPort != 53) continue
            val dns = buf.copyOfRange(ihl + 8, n)
            val domain = parseDnsQName(dns) ?: continue
            record(domain)
            Logx.i("Vpn", "DNS query: $domain")
            // 转发真实 DNS(8.8.8.8) 并回填应答
            try {
                udp.send(DatagramPacket(dns, dns.size, InetSocketAddress("8.8.8.8", 53)))
                val rbuf = ByteArray(2048)
                val rp = DatagramPacket(rbuf, rbuf.size)
                udp.soTimeout = 3000
                udp.receive(rp)
                val reply = buildUdpIpPacket(buf, ihl, rbuf.copyOf(rp.length))
                out.write(reply)
                out.flush()
            } catch (e: Exception) {
                Logx.i("Vpn", "DNS forward fail for $domain: ${e.javaClass.simpleName}")
            }
        }
        Logx.i("Vpn", "DNS observer stopped")
        running = false
    }

    /** 解析 DNS 查询名（QNAME 起始处，兼容指针跳过） */
    private fun parseDnsQName(dns: ByteArray): String? {
        if (dns.size < 13) return null
        var i = 12
        val sb = StringBuilder()
        while (i < dns.size) {
            val len = dns[i].toInt() and 0xFF
            if (len == 0) { i++; break }
            if (len >= 0xC0) return sb.toString().ifEmpty { null }  // 压缩指针：取已有部分
            i++
            if (i + len > dns.size) return null
            sb.append(String(dns, i, len)); sb.append('.')
            i += len
        }
        return sb.toString().trimEnd('.').ifEmpty { null }
    }

    /** 交换原包构成 UDP-over-IPv4 应答（源目对调，重算 IP 头校验和，UDP 长度更新） */
    private fun buildUdpIpPacket(req: ByteArray, ihl: Int, payload: ByteArray): ByteArray {
        val total = 20 + 8 + payload.size
        val p = ByteArray(total)
        p[0] = 0x45; p[1] = 0
        p[2] = (total shr 8).toByte(); p[3] = total.toByte()
        p[8] = 64; p[9] = 17
        // 源=原目的(10.111.222.53)，目=原源
        System.arraycopy(req, ihl + 16, p, 12, 4)   // 原 dst -> 新 src
        System.arraycopy(req, ihl + 12, p, 16, 4)   // 原 src -> 新 dst
        var sum = 0L
        for (i in 0 until 20 step 2) { sum += ((p[i].toInt() and 0xFF) shl 8) or (p[i + 1].toInt() and 0xFF) }
        while (sum shr 16 != 0L) sum = (sum and 0xFFFF) + (sum shr 16)
        val c = (sum.inv() and 0xFFFF).toLong()
        p[10] = (c shr 8).toByte(); p[11] = c.toByte()
        // UDP 头：端口对调，长度
        p[20] = req[ihl + 2]; p[21] = req[ihl + 3]
        p[22] = req[ihl]; p[23] = req[ihl + 1]
        val ul = 8 + payload.size
        p[24] = (ul shr 8).toByte(); p[25] = ul.toByte()
        System.arraycopy(payload, 0, p, 28, payload.size)
        return p
    }

    // protect(DatagramSocket) 是 VpnService 公开 API，避免回环路由

    override fun onDestroy() { running = false; super.onDestroy() }
}
