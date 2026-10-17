# RayNeo V3 BLE JSON 协议（真机实测 + 源码逆向双证）

基线：cn.rayneo.mercury v3.0.5；真机 V3（ctModel=XRGF50，MAC 84:15:71:AB:EF:AE）
本项目 App：rayneo-bridge v0.1.6+（仓库根目录）

## 1. 传输层（真机实测确认）
- BLE GATT service `0000b81d-0000-1000-8000-00805f9b34fb`
- write（手机→眼镜）`ea8b70d5-2bd3-49ab-9c31-9c38b2c3c4f9`
- receive（眼镜→手机 notify）`7db3e235-3608-41f3-a03c-955fcbd2ea4b`
- V3 上 heart(FFF6) 不存在；协议表含 glassUrgent（他型号用）
- 默认 MTU：ATT 包 20 字节 = 1B 计数器 + 19B 数据

## 2. 分片规则（实测推定）
- 眼镜→手机：每个 ATT 通知首字节 = 循环计数器（观测 01,02,00… 模3）
- 消息边界**不依赖计数器**：重组器 v2 = 剥首字节 + JSON 括号平衡定界（字符串感知）
- 手机→眼镜（v0.1.4 实现）：JSON 按 19B 分片，首字节 01,02,…,00(尾)，逐片 write 间隔 40ms

## 3. 消息信封（源码逆向）
- 请求基类 BleRequest：`id`(UUID)、`uri`("ble.request.xxx")、`device`；kotlinx.serialization
- 眼镜状态推送实测样本（截断）：
  - `{"generalStatus":{"Bright":false,"mcuS…":"ble.request.gener…`
  - `{"generalSettings":{"ctModel":"XRGF50",…`

## 4. 双链路与鉴权（详见 DESIGN.md）
- V3 官方默认 BLE 优先；SPP 直连被拒（read ret:-1），控制链以 BLE 为准
- V3 豁免 connection-pairing 鉴权；BLE JSON 通道实测明文
- 二进制帧（SPP/业务通道）：HEAD 0xAB55/0x55AA + Type + Len + MsgNum + Addr + BusinessID + Payload + CRC16-XMODEM

## 5. 操作指令面（strings 全量枚举 165 条，完整清单见附录）
- 状态/设置：`ble.request.general.status` / `general.settings` / `general.settings.storage`
- **TTS 朗读：`ble.request.online_tts`**（另有 `switch_speech_engine` / `speech_config` / `ai_speech_mode`）
- 导航/效率：`nav` / `nav.heartbeat` / `notes` / `countdown` / `shortcuts`
- 心跳：`new_heartbeat`；网络：`net_config` / `scan_wifi_result` / `wifi.connect_state` / `tethering`
- 音乐/电台：`get_stream_music_type` / `stream_music_state` / `radio_station_state` / `qq_music_execute`
- 影像：`camera_config` / `camera_status` / `mix_shot` / `mix_record` / `gallery`
- AI：`switch_ray_neo_ai` / `switch_vqa` / `agent_store_state`
- 支付：`alipay_*` 全家；系统：`auth` / `auth_device_info` / `verify_version` / `factory_reset` / `fota` / `debug` / `logcat_*`
- 每个 request 均有对应 `ble.response.xxx`；payload 结构类可按 URI 在 dump 里反查 @Serializable

## 6. 真机验证状态（截至 v0.1.6）
- ✅ GATT 连接/服务发现/通知订阅/写特征值
- ✅ 状态推送抓取+重组（generalStatus/generalSettings 样本在手）
- ✅ AB55 心跳帧发送触发推送
- ⏳ 需真机在场的下一步：官方 App 对照抓包标定每个 uri 的 payload → `online_tts` 让眼镜开口 → assistant 通道接管（BusinessID=14，DESIGN.md §4）

## 附录：完整 URI 清单（165 条）
见同目录 uri_list.txt
