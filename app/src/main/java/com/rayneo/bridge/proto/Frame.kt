package com.rayneo.bridge.proto

/**
 * 雷鸟 coreconnect 线协议帧（逆向自 cn.rayneo.mercury v3.0.5，DESIGN.md §3）
 *
 * HEAD(2 BE) + PacketType(1) + Length(2 BE) + MsgNum(2 BE) + Address(1) + BusinessId(1) + Payload + CRC16(2 BE)
 * HEAD: 请求 0xAB55 / 响应 0x55AA；CRC16-XMODEM(poly 0x1021, init 0)
 *
 * TODO(真机校准)：Length 语义、CRC 覆盖范围、Address 取值——首版按上式实现，联调时用官方 App 抓包校准。
 */
data class Frame(
    val isResponse: Boolean,
    val packetType: Int,
    val msgNum: Int,
    val address: Int,
    val businessId: Int,
    val payload: ByteArray
) {
    companion object {
        const val HEAD_REQUEST = 0xAB55
        const val HEAD_RESPONSE = 0x55AA
        const val HEADER_SIZE = 9   // head2+type1+len2+msg2+addr1+bid1
        const val MIN_PACKET_SIZE = 10 // 逆向：协议最小包
        const val MAX_PAYLOAD = 4096

        // 业务通道（BusinessID，逆向 DESIGN.md §3）
        const val BIZ_CONNECTION_PAIRING = 17
        const val BIZ_HEARTBEAT = 13
        const val BIZ_VOICE_ASSISTANT = 14
        const val BIZ_FILE_TRANSFER = 18
        const val BIZ_NOTIFICATION = 21

        fun decode(buf: ByteArray): Frame? {
            if (buf.size < MIN_PACKET_SIZE) return null
            val head = ((buf[0].toInt() and 0xFF) shl 8) or (buf[1].toInt() and 0xFF)
            val isResp = when (head) {
                HEAD_RESPONSE -> true
                HEAD_REQUEST -> false
                else -> return null
            }
            val type = buf[2].toInt() and 0xFF
            val len = ((buf[3].toInt() and 0xFF) shl 8) or (buf[4].toInt() and 0xFF)
            val msgNum = ((buf[5].toInt() and 0xFF) shl 8) or (buf[6].toInt() and 0xFF)
            val addr = buf[7].toInt() and 0xFF
            val biz = buf[8].toInt() and 0xFF
            val total = HEADER_SIZE + len + 2
            if (buf.size < total || len > MAX_PAYLOAD) return null
            val payload = buf.copyOfRange(HEADER_SIZE, HEADER_SIZE + len)
            // CRC 校验（范围待真机校准：先按 type..payload 校验，错帧只记日志不致命）
            val crc = ((buf[HEADER_SIZE + len].toInt() and 0xFF) shl 8) or (buf[HEADER_SIZE + len + 1].toInt() and 0xFF)
            val calc = crc16Xmodem(buf, 2, HEADER_SIZE + len - 2)
            if (crc != calc) {
                com.rayneo.bridge.util.Logx.e("Frame", "CRC mismatch got=0x%04X calc=0x%04X len=%d".format(crc, calc, len))
            }
            return Frame(isResp, type, msgNum, addr, biz, payload)
        }

        fun encode(f: Frame): ByteArray {
            val len = f.payload.size
            val out = ByteArray(HEADER_SIZE + len + 2)
            val head = if (f.isResponse) HEAD_RESPONSE else HEAD_REQUEST
            out[0] = (head shr 8).toByte(); out[1] = head.toByte()
            out[2] = f.packetType.toByte()
            out[3] = (len shr 8).toByte(); out[4] = len.toByte()
            out[5] = (f.msgNum shr 8).toByte(); out[6] = f.msgNum.toByte()
            out[7] = f.address.toByte(); out[8] = f.businessId.toByte()
            f.payload.copyInto(out, HEADER_SIZE)
            val crc = crc16Xmodem(out, 2, HEADER_SIZE + len - 2)
            out[HEADER_SIZE + len] = (crc shr 8).toByte()
            out[HEADER_SIZE + len + 1] = crc.toByte()
            return out
        }

        /** CRC16-XMODEM，poly 0x1021，init 0（逆向自 utils/Crc16Util） */
        fun crc16Xmodem(buf: ByteArray, off: Int, n: Int): Int {
            var crc = 0
            for (i in off until off + n) {
                crc = crc xor ((buf[i].toInt() and 0xFF) shl 8)
                repeat(8) {
                    crc = if (crc and 0x8000 != 0) (crc shl 1) xor 0x1021 else crc shl 1
                    crc = crc and 0xFFFF
                }
            }
            return crc
        }
    }

    override fun equals(other: Any?): Boolean = other is Frame && other.payload.contentEquals(payload)
    override fun hashCode(): Int = payload.contentHashCode()
}
