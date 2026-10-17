# RayNeo V3 眼镜 × 自有大模型 桥接项目

目标：手机侧自研服务连接雷鸟 V3 AR 眼镜，接管其语音助手管线，接入任意 LLM（OpenAI 兼容接口），
不依赖雷鸟官方 App 的云端（voice-assistant-api.rayneo.cn）。

逆向基线：cn.rayneo.mercury v3.0.5（versionCode 1603，APK SHA-256 e3c40aa8cec6f3cd70d9c48b1a81ad06795ee17119d7d576b3cb49f6ee7fe786）
证据与源码 dump：Termux ~/rayneo/（pkg_summary.json / all_strings.txt / src / src_conn）

---

## 1. 连接 SDK 架构（com.ffalconxr.mercury.coreconnect，268 个顶层类）

链路分层 RNLinkStrategy：
- LOW_BLE：BLE GATT 控制链（常驻低功耗）
- HIGH_AP：高速链 = SPP/RFCOMM（RNSppAutoReconnector.ensureHighApLink）
- Wi-Fi 热点：照片/文件传输（strings 证据：softap_ip / LocalOnlyHotspot / enable_hotspot_ap / photoTrans*）

启动链路：startConnect → requestCompanionAssociationBeforeConnect（未配对走系统 CDM 关联，已配对跳过）
→ startBleConnect → BLE 链路上 TLV 设备信息交换（眼镜下发 localDeviceName + ECDH 公钥65B + SN）
→ 鉴权 → connectMissingLinkersAfterAuth 补齐双链。

自动重连（connectAuto）：
- V4：强制 BLE 优先（避 BR 冲突）
- 已配对且 BR 已连（HFP/ACL）：SPP 优先，失败回退 BLE
- 其余：BLE
- **SPP 白名单：仅 V4 / VENUS / VENUS_INTL / V3**（X2/X3/V3SLIM 无 SPP）

## 2. 鉴权（RNAuthManager，双向挑战应答）

型号表 RNModelType（companyId=5716）：X2(0001)/V3(0002)/X3(0003)/V3SLIM(0004)/V4(0005)/VENUS(0006)/VENUS_INTL(0007)

- **V3 豁免 connection-pairing 鉴权**（requiresConnectionPairingAuthentication = modelType != V3，RNAuthManager:706）
- 老路径（无 accountId）：authValue = SHA-256(random1 ‖ phoneId ‖ glassMAC ‖ STATIC_KEY16B硬编码)
- 新路径（有 accountId）：ECDH secp256r1（眼镜 TLV 下发65B 未压缩公钥）→
  bondKey = hex(SHA-256(ECDH共享 ‖ STATIC_KEY ‖ accountId)) 持久化 →
  authValue = SHA-256(random1 ‖ phoneId ‖ glassMAC ‖ accountId ‖ bondKey)
- 流程：手机发 random1(4B)+authValue1 → 眼镜回 random2+authValue2 → 手机验值 →
  会话密钥 = SHA-256(random1 ‖ random2 ‖ phoneId ‖ glassMAC ‖ STATIC_KEY [‖ bondKey])
- 会话密钥在 SDK 内未发现用于消息加密（消息疑似明文 TLV，PacketType 位域有保留位，待实测确认）
- 安全注记：debug 日志明文打印 BondKey/SessionKey/AuthValue hex（RNLog）

## 3. 线协议（SPP/BLE 共用，protocal/ 包）

帧：HEAD(2)+PacketType(1)+Length(2)+MsgNum(2)+Address(1)+BusinessID(1)+…+CRC16(2)，最小10字节
魔数：请求 0xAB55，响应 0x55AA；CRC16-XMODEM（poly 0x1021, init 0）；PacketType 位域含分片(sliceInfoLength)
业务通道24路（BusinessID）：17=CONNECTION_PAIRING(鉴权) 13=HEARTBEAT 14=VOICE_ASSISTANT 18=FILE_TRANSFER
9/10/11=MARS_STREAMING/FOTA/PHOTO_TRANSFER 19=AI_SUBTITLE 21=NOTIFICATION 23=PROACTIVE_AI …

BLE 特征值（DeviceUUIDType）：
- service 0000b81d-0000-1000-8000-00805f9b34fb
- write(手机→眼镜) ea8b70d5-2bd3-49ab-9c31-9c38b2c3c4f9
- receive(眼镜→手机) 7db3e235-3608-41f3-a03c-955fcbd2ea4b
- heart/urgent 0000FFF6-0000-1000-8000-00805F9B34FB；paired EA8B60C5-2BD3-49AB-9C31-9D38B1C5C5F9
- 写策略：回调驱动 / 线性退避；MTU 协商（BleMtuResolver）；消息队列
SPP：标准 00001101-0000-1000-8000-00805F9B34FB（insecure RFCOMM 变体）+ 自动重连器
广播协议 V1/V3 两代（RNBleAdvResDataV1/V3）；音频挂 HFP/A2DP；眼镜主控恒玄 BES 芯片（BES 日志指令 161）

## 4. 语音助手管线（关键：手机侧执行）BusinessID=14

AssistantMsgConstants 消息类型表（眼镜↔手机）：
- 1 glass-唤醒请求 / 8 glass-退出请求 / **3 glass-ASR音频流（眼镜麦克风 PCM 发给手机）**
- 35 glass-VQA图片流 / 33 glass-技能执行回调 / 51 备忘录打断 / 54 glass state / 240 唤醒音频回流
- **5 phone-ASR文本** / 6 phone-TTS状态 / 2 phone-录音开关 / 4 phone-VAD状态 / 7 phone-退出 / 9 phone-会话状态
- 32 phone-技能协议 / 34 phone-VQA拍照 / 50 备忘料事件 / 52 电台/Agent事件 / 36-37 live AI

负载：SpeechTransportPayload{rc, text, timbre(音色), work_mode, offline}；眼镜侧离线 TTS（OfflineTTSBean）

官方云端（app 内 SDK，com.rayneo.airuntime）：
- POST https://voice-assistant-api-{t,p}.rayneo.cn/auth/initSdk（SDK 初始化，见 airuntime/auth/d.java,g.java）
- 音频数据 https://xr-audio-data-{t,p}.rayneo.cn/adapi/
- LLM 模型选择：ConfigurationProvider.LlmModel{DEFAULT(""), JIU_TIAN("jiutian-lan")}（九天-蓝）
- NLP 意图拦截点：INlpInterceptor.intercept(NlpResult)→true 即本地接管（"眼镜调用接口/技能"即挂这里）
- app 侧封装：cn.rayneo.mercury.connectivity.coreconnect.CoreConnectManager + airuntime SpeechSdkManager/AssistantController

## 5. 本项目路线（自研接管）

V3 走 SPP 白名单 + 鉴权豁免 → 最短路径：
1. Android App（Kotlin）：实现/移植 coreconnect 连接（SPP 优先），复用系统已配对 bond（insecure RFCOMM 无需重新配对）
2. 承接 BusinessID=14 助手消息：收 type3 音频 → 自有 ASR（可先 FunASR/whisper API）→ 自有 LLM（OpenAI 兼容端点，模型可配）
   → 回 type5 ASR 文本 + type6 TTS 状态 + SpeechTransportPayload{text,timbre}（眼镜离线 TTS 念出来）
3. 端点/密钥可配置（本地 HTTP 配置页）；不劫持官方 App（两 App 并存时 SPP socket 竞争待实测）

备选路线（记录，不首选）：
- 官方 App + 代理改写 voice-assistant-api 响应（TLS pinning 未知，脆弱）
- 直接仿写 RayNeo 云 SDK 协议（/auth/initSdk + adapi，Tencent QCloud 系，工作量大）

## 6. 待验证清单（真机联调阶段）

- [ ] V3 广播里 modelId/adv 格式（V1 还是 V3 代协议）
- [ ] V3 的 auth 流程实际收发（豁免 connection-pairing 后走什么消息）
- [ ] SPP socket 建立细节（UUID、insecure、端口）与消息是否加密
- [ ] type3 音频格式（采样率/编码/分片）
- [ ] 官方 App 与自研 App 同时连接时的 socket/ACL 行为
- [ ] TTS：text 回发后眼镜离线 TTS 的音色参数（timbre）取值

## 进度快照
- 2026-10-17：逆向完成（连接/鉴权/协议/助手管线全量），仓库建立。源码 dump：~/rayneo/src(32300文件,--show-bad-code修复版) + ~/rayneo/src_conn(SDK)。下一步：Android 工程骨架 + SPP 连通性实验。
