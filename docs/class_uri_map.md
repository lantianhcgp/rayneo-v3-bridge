# phone↔glass 直发消息类映射（src9 提取）

## MessageKt URI 常量表

URI_PULL_RAYNEO_DJ_SWITCH = ble.response.pull_rayneo_dj_switch
URI_PULL_STREAM_MUSIC_CONTROL = ble.response.stream_music_control
URI_PULL_USER_INFO = ble.response.set_user_info
URI_REQUEST_ALIPAY_DEVICE_LINK = ble.request.alipay_device_link
URI_REQUEST_ALIPAY_PAYMENT = ble.request.alipay_payment
URI_REQUEST_ALIPAY_PROTOCOL = ble.request.alipay_protocol
URI_REQUEST_ALIPAY_STATUS = ble.request.alipay_status
URI_REQUEST_CAMERA_STATUS = ble.request.camera_status
URI_REQUEST_FINISH_FULL_LOG = ble.request.finish_full_log
URI_REQUEST_GENERAL_SETTINGS_STORAGE = ble.request.general.settings.storage
URI_REQUEST_ONLINE_TTS = ble.request.online_tts
URI_REQUEST_PROMPTER_MESSAGE = ble.response.prompter_message
URI_REQUEST_START_FULL_LOG_SUCCESS = ble.request.start_full_log_success
URI_RESPONSE_ACCOUNT = ble.response.account
URI_RESPONSE_ACTION_BUTTON = ble.response.action_button
URI_RESPONSE_AGENT_START = ble.response.agent_start
URI_RESPONSE_AGENT_STATE = ble.response.agent_store_state
URI_RESPONSE_AI_SPEECH_MODE = ble.response.ai_speech_mode
URI_RESPONSE_ALIPAY_BIND = ble.response.alipay_bind
URI_RESPONSE_ALIPAY_BIND_STATUS = ble.response.alipay_bind_status
URI_RESPONSE_ALIPAY_DEVICE_LINK = ble.response.alipay_device_link
URI_RESPONSE_ALIPAY_GET_SCHEME = ble.response.alipay_common_scheme
URI_RESPONSE_ALIPAY_PAYMENT = ble.response.alipay_payment
URI_RESPONSE_ALIPAY_PROTOCOL = ble.response.alipay_protocol
URI_RESPONSE_ALIPAY_TUTORIAL = ble.response.alipay_tutorial
URI_RESPONSE_ALIPAY_UNBIND = ble.response.alipay_unbind
URI_RESPONSE_ANALYTICS = ble.response.analytics
URI_RESPONSE_APP_MARKET = ble.response.app_market
URI_RESPONSE_AUTH = ble.response.auth
URI_RESPONSE_AUTO_LOCK_TIME = ble.response.auto_lock_time
URI_RESPONSE_BLUETOOTH_STATE = ble.response.bluetooth_state
URI_RESPONSE_CAMERA_CONFIG = ble.response.camera_config
URI_RESPONSE_CLIENT_CLOSE_CONFIRM = ble.response.client.close_confirm
URI_RESPONSE_CONNECT_WIFI = ble.response.wifi.connect
URI_RESPONSE_COUNTDOWN = ble.response.countdown
URI_RESPONSE_DEBUG = ble.response.debug
URI_RESPONSE_DEVICE_INFO = ble.response.stream_device_info
URI_RESPONSE_EXEC_QQ_MUSIC_RESULT = ble.response.exec_qq_music_result
URI_RESPONSE_FACTORY_RESET = ble.response.factory_reset
URI_RESPONSE_FAVORITE_STREAM_MUSIC = ble.response.favorite_stream_music
URI_RESPONSE_FOTA = ble.response.fota
URI_RESPONSE_FULL_LOG_STATUS = ble.response.full_log_status
URI_RESPONSE_GALLERY = ble.response.gallery
URI_RESPONSE_GENERAL_SETTINGS_STORAGE = ble.response.general.settings.storage
URI_RESPONSE_GEO_CONFIG = ble.response.geo_config
URI_RESPONSE_GET_STREAM_MUSIC_TYPE = ble.response.get_stream_music_type
URI_RESPONSE_GLASSES_BOX_STATE = ble.response.glasses_box_state
URI_RESPONSE_GYRO_CAL = ble.response.gyro.cal
URI_RESPONSE_HAVE_MFI_CHIP = ble.response.have_mfi_chip
URI_RESPONSE_HEARTBEAT = ble.response.new_heartbeat
URI_RESPONSE_INPUT_INTERACT_CONFIG = ble.response.input_interact_config
URI_RESPONSE_KEYBOARD = ble.response.keyboard
URI_RESPONSE_LOCATION = ble.response.location
URI_RESPONSE_LOGCAT = ble.response.do_logcat
URI_RESPONSE_LOG_SEND_TASK = ble.response.log_send_task
URI_RESPONSE_MIX_RECORD = ble.response.mix_record
URI_RESPONSE_MIX_SHOT = ble.response.mix_shot
URI_RESPONSE_MODIFY_NAME = ble.response.modify_name
URI_RESPONSE_MULTI_DEVICE = ble.response.multi_device
URI_RESPONSE_NAV_HEARTBEAT = ble.response.nav.heartbeat
URI_RESPONSE_NAV_ROUTE = ble.response.nav.route
URI_RESPONSE_NET_CONFIG = ble.response.net_config
URI_RESPONSE_NOTES = ble.response.notes
URI_RESPONSE_NOTI_APPS = ble.response.noti.apps
URI_RESPONSE_PHONE_RECORD_STATE = ble.response.phone_record_state
URI_RESPONSE_POST_NOTIFICATION = ble.response.notification.post
URI_RESPONSE_PROMPTER_FILE = ble.response.prompter_file
URI_RESPONSE_PROMPTER_MESSAGE = ble.response.prompter_message
URI_RESPONSE_PULL_GENERAL_SETTINGS = ble.response.pull.general_settings
URI_RESPONSE_PULL_GENERAL_STATUS = ble.response.pull.general_status
URI_RESPONSE_QQ_STREAM_MUSIC_STATE = ble.response.qq_stream_music_state
URI_RESPONSE_RADIO_STATION_PLAY = ble.response.radio_station_play
URI_RESPONSE_RADIO_STATION_PREFERENCE = ble.response.radio_station_preference
URI_RESPONSE_RADIO_STATION_STATE = ble.response.radio_station_state
URI_RESPONSE_REMOVE_WIFI = ble.response.wifi.remove
URI_RESPONSE_SCAN_WIFI = ble.response.wifi.scan
URI_RESPONSE_SERVER_CLOSE = ble.response.server.close
URI_RESPONSE_SET_LANGUAGE = ble.response.set_language
URI_RESPONSE_SET_STREAM_MUSIC_TYPE = ble.response.set_stream_music_type
URI_RESPONSE_SET_TIME = ble.response.set_time
URI_RESPONSE_SHORTCUTS = ble.response.shortcuts
URI_RESPONSE_SPEECH_CONFIG = ble.response.speech_config
URI_RESPONSE_START_FULL_LOG = ble.response.start_full_log
URI_RESPONSE_STREAM_MUSIC_AUTH = ble.response.stream_music_auth
URI_RESPONSE_STREAM_MUSIC_STATE = ble.response.stream_music_state
URI_RESPONSE_SWITCH_AI_AUDIO_PLAN = ble.response.switch_ai_audio_plan
URI_RESPONSE_SWITCH_RAY_NEO_AI = ble.response.switch_ray_neo_ai
URI_RESPONSE_SWITCH_SPEECH_ENGINE = ble.response.switch_speech_engine
URI_RESPONSE_TETHERING = ble.response.tethering
URI_RESPONSE_TOGGLE_WIFI = ble.response.wifi.toggle
URI_RESPONSE_TRANSLATION_AUDIO_PARAM = ble.response.translation_audio_param
URI_RESPONSE_UPDATE_GENERAL_STATUS = ble.response.general.status.update
URI_RESPONSE_USER_GUIDE = ble.response.user_guide
URI_RESPONSE_VERIFY_VERSION = ble.response.verify_version
URI_RESPONSE_WINDOW_SCALE_CONFIG = ble.response.window_scale_config
URI_SWITCH_RAYNEO_DJ = ble.response.switch_rayneo_dj
URI_REQUEST_SCAN_WIFI_RESULT = ble.request.scan_wifi_result
URI_REQUEST_GENERAL_STATUS = ble.request.general.status
URI_REQUEST_REAL_BLUETOOTH_ADDRESS = ble.request.bluetooth.real_address
URI_REQUEST_GENERAL_SETTINGS = ble.request.general.settings
URI_REQUEST_SERVER_CLOSE_CONFIRM = ble.request.server.close_confirm
URI_REQUEST_GYRO_CAL_STATUS = ble.request.gyro.cal.status
URI_REQUEST_RING_INFO = ble.request.ring.info
URI_REQUEST_WIFI_CONNECT_STATE = ble.request.wifi.connect_state
URI_REQUEST_CLIENT_CLOSE = ble.request.client.close
URI_REQUEST_GALLERY = ble.request.gallery
URI_REQUEST_NAV = ble.request.nav
URI_REQUEST_VOLUME_SYNC_SWITCH_TIP_DIALOG = ble.request.volume_tips_dialog
URI_REQUEST_NAV_HEARTBEAT = ble.request.nav.heartbeat
URI_REQUEST_HEARTBEAT = ble.request.new_heartbeat
URI_REQUEST_APP_MARKET = ble.request.app_market
URI_REQUEST_SEND_QR_CODE = ble.request.ring.send_qr_code
URI_REQUEST_SHORTCUTS = ble.request.shortcuts
URI_REQUEST_TETHERING = ble.request.tethering
URI_REQUEST_MIX_SHOT = ble.request.mix_shot
URI_REQUEST_MIX_RECORD = ble.request.mix_record
URI_REQUEST_VERIFY_VERSION = ble.request.verify_version
URI_REQUEST_LOGCAT = ble.request.logcat_result
URI_REQUEST_LOGCAT_IND = ble.request.logcat_ind
URI_REQUEST_NET_CONFIG = ble.request.net_config
URI_REQUEST_TOUCH = ble.request.touch
URI_REQUEST_NOTES = ble.request.notes
URI_REQUEST_MULTI_DEVICE = ble.request.multi_device
URI_REQUEST_ACCOUNT = ble.request.account
URI_REQUEST_FOTA = ble.request.fota
URI_REQUEST_USER_GUIDE = ble.request.user_guide
URI_REQUEST_ACTION_BUTTON = ble.request.action_button
URI_REQUEST_DEVICE_INFO = ble.request.stream_device_info
URI_REQUEST_STREAM_MUSIC_AUTH = ble.request.stream_music_auth
URI_REQUEST_STREAM_MUSIC_STATE = ble.request.stream_music_state
URI_REQUEST_DEBUG = ble.request.debug
URI_PUSH_RAYNEO_DJ_SWITCH = ble.request.pull_rayneo_dj_switch
URI_REQUEST_PUSH_USER_INFO = ble.request.set_user_info
URI_REQUEST_INPUT_INTERACT_CONFIG = ble.request.input_interact_config
URI_REQUEST_CAMERA_CONFIG = ble.request.camera_config
URI_REQUEST_SPEECH_CONFIG = ble.request.speech_config
URI_REQUEST_AI_SPEECH_MODE = ble.request.ai_speech_mode
URI_REQUEST_PHONE_RECORD_STATE = ble.request.phone_record_state
URI_REQUEST_GLASSES_BOX_STATE = ble.request.glasses_box_state
URI_REQUEST_FACTORY_RESET = ble.request.factory_reset
URI_REQUEST_BLUETOOTH_STATE = ble.request.bluetooth_state
URI_REQUEST_SWITCH_RAY_NEO_AI = ble.request.switch_ray_neo_ai
URI_REQUEST_SWITCH_AI_AUDIO_PLAN = ble.request.switch_ai_audio_plan
URI_REQUEST_LOG_TASK_PROGRESS = ble.request.log_task_progress
URI_REQUEST_FULL_LOG_STATUS = ble.request.full_log_status
URI_REQUEST_RADIO_STATION_STATE = ble.request.radio_station_state
URI_REQUEST_GEO_CONFIG = ble.request.geo_config
URI_REQUEST_COUNTDOWN = ble.request.countdown
URI_REQUEST_SPEECH_ENGINE = ble.request.switch_speech_engine
URI_REQUEST_ALIPAY_BIND = ble.request.alipay_bind
URI_REQUEST_ALIPAY_BIND_STATUS = ble.request.alipay_bind_status
URI_REQUEST_ALIPAY_UNBIND = ble.request.alipay_unbind
URI_REQUEST_ALIPAY_TUTORIAL = ble.request.alipay_tutorial
URI_REQUEST_ALIPAY_SCHEME = ble.request.alipay_common_scheme
URI_REQUEST_AGENT_STATE = ble.request.agent_store_state
URI_REQUEST_HAVE_MFI_CHIP = ble.request.have_mfi_chip
URI_REQUEST_STREAM_MUSIC_TYPE = ble.request.get_stream_music_type
URI_REQUEST_QQ_MUSIC_EXECUTE = ble.request.qq_music_execute
URI_REQUEST_WINDOW_SCALE_CONFIG = ble.request.window_scale_config
URI_REQUEST_AUTH = ble.request.auth
URI_REQUEST_AUTH_DEVICE_INFO = ble.request.auth_device_info

## Request/Response 类 → URI

AISpeechConfigRequest → MessageKt.URI_REQUEST_SPEECH_CONFIG (Payload)
AISpeechConfigResponse → MessageKt.URI_RESPONSE_SPEECH_CONFIG (Payload)
AISpeechModeRequest → MessageKt.URI_REQUEST_AI_SPEECH_MODE (Payload)
AISpeechModeResponse → MessageKt.URI_RESPONSE_AI_SPEECH_MODE (Payload)
AccountInfoRequest → MessageKt.URI_REQUEST_ACCOUNT (Payload)
AccountInfoResponse → MessageKt.URI_RESPONSE_ACCOUNT (Payload)
ActionButtonRequest → MessageKt.URI_REQUEST_ACTION_BUTTON (Payload)
ActionButtonResponse → MessageKt.URI_RESPONSE_ACTION_BUTTON (Payload)
AgentStartResponse → MessageKt.URI_RESPONSE_AGENT_START (Payload)
AgentStoreStateRequest → MessageKt.URI_REQUEST_AGENT_STATE (Payload)
AgentStoreStateResponse → MessageKt.URI_RESPONSE_AGENT_STATE (Payload)
AliPayBindRequest → MessageKt.URI_REQUEST_ALIPAY_BIND (Payload)
AliPayBindResponse → MessageKt.URI_RESPONSE_ALIPAY_BIND (Payload)
AliPayBindStatusRequest → MessageKt.URI_REQUEST_ALIPAY_BIND_STATUS (Payload)
AliPayBindStatusResponse → MessageKt.URI_RESPONSE_ALIPAY_BIND_STATUS (Payload)
AliPayGetSchemeResponse → MessageKt.URI_RESPONSE_ALIPAY_GET_SCHEME (Payload)
AliPaySchemeRequest → MessageKt.URI_REQUEST_ALIPAY_SCHEME (Payload)
AliPayTutorialRequest → MessageKt.URI_REQUEST_ALIPAY_TUTORIAL (Payload)
AliPayTutorialResponse → MessageKt.URI_RESPONSE_ALIPAY_TUTORIAL (Payload)
AliPayUnbindRequest → MessageKt.URI_REQUEST_ALIPAY_UNBIND (Payload)
AliPayUnbindResponse → MessageKt.URI_RESPONSE_ALIPAY_UNBIND (Payload)
AlipayDeviceLinkRequest → MessageKt.URI_REQUEST_ALIPAY_DEVICE_LINK (Payload)
AlipayDeviceLinkResponse → MessageKt.URI_RESPONSE_ALIPAY_DEVICE_LINK (Payload)
AlipayPaymentRequest → MessageKt.URI_REQUEST_ALIPAY_PAYMENT (Payload)
AlipayPaymentResponse → MessageKt.URI_RESPONSE_ALIPAY_PAYMENT (Payload)
AlipayProtocolRequest → MessageKt.URI_REQUEST_ALIPAY_PROTOCOL (Payload)
AlipayProtocolResponse → MessageKt.URI_RESPONSE_ALIPAY_PROTOCOL (Payload)
AlipayRecordRequest → ? (其他)
AlipayStatusRequest → MessageKt.URI_REQUEST_ALIPAY_STATUS (Payload)
AnalyticsResponse → MessageKt.URI_RESPONSE_ANALYTICS (Payload)
AppMarketRequest → MessageKt.URI_REQUEST_APP_MARKET (Payload)
AppMarketResponse → MessageKt.URI_RESPONSE_APP_MARKET (Payload)
AuthDeviceInfoExchangeRequest → MessageKt.URI_REQUEST_AUTH_DEVICE_INFO (Payload)
AuthDeviceInfoExchangeResponse → MessageKt.URI_REQUEST_AUTH_DEVICE_INFO (其他)
AuthInfoRequest → MessageKt.URI_REQUEST_AUTH (Payload)
AuthInfoResponse → MessageKt.URI_RESPONSE_AUTH (Payload)
AutoLockTimeResponse → MessageKt.URI_RESPONSE_AUTO_LOCK_TIME (其他)
BleRequest → ? (其他)
BleResponse → ? (其他)
BluetoothStateRequest → MessageKt.URI_REQUEST_BLUETOOTH_STATE (Payload)
BluetoothStateResponse → MessageKt.URI_RESPONSE_BLUETOOTH_STATE (Payload)
CameraAppConfigRequest → MessageKt.URI_REQUEST_CAMERA_CONFIG (Payload)
CameraAppConfigResponse → MessageKt.URI_RESPONSE_CAMERA_CONFIG (Payload)
CameraStatusRequest → MessageKt.URI_REQUEST_CAMERA_STATUS (Payload)
ClientCloseConfirmResponse → MessageKt.URI_RESPONSE_CLIENT_CLOSE_CONFIRM (其他)
ClientCloseRequest → MessageKt.URI_REQUEST_CLIENT_CLOSE (其他)
ConnectWifiResponse → MessageKt.URI_RESPONSE_CONNECT_WIFI (其他)
CountdownRequest → MessageKt.URI_REQUEST_COUNTDOWN (Payload)
CountdownResponse → MessageKt.URI_RESPONSE_COUNTDOWN (Payload)
DebugRequest → MessageKt.URI_REQUEST_DEBUG (Payload)
DebugResponse → MessageKt.URI_RESPONSE_DEBUG (Payload)
DeviceInfoResponse → MessageKt.URI_RESPONSE_DEVICE_INFO (其他)
FactoryResetRequest → MessageKt.URI_REQUEST_FACTORY_RESET (Payload)
FactoryResetResponse → MessageKt.URI_RESPONSE_FACTORY_RESET (Payload)
FinishFullLogRequest → MessageKt.URI_REQUEST_FINISH_FULL_LOG (其他)
FotaRequest → MessageKt.URI_REQUEST_FOTA (Payload)
FotaResponse → MessageKt.URI_RESPONSE_FOTA (Payload)
FullLogStatusRequest → MessageKt.URI_REQUEST_FULL_LOG_STATUS (Payload)
FullLogStatusResponse → MessageKt.URI_RESPONSE_FULL_LOG_STATUS (Payload)
GalleryRequest → MessageKt.URI_REQUEST_GALLERY (Payload)
GalleryResponse → MessageKt.URI_RESPONSE_GALLERY (Payload)
GeneralSettingsRequest → MessageKt.URI_REQUEST_GENERAL_SETTINGS (其他)
GeneralSettingsStorageRequest → MessageKt.URI_REQUEST_GENERAL_SETTINGS_STORAGE (其他)
GeneralSettingsStorageResponse → MessageKt.URI_RESPONSE_GENERAL_SETTINGS_STORAGE (其他)
GeneralStatusRequest → MessageKt.URI_REQUEST_GENERAL_STATUS (其他)
GeoConfigRequest → MessageKt.URI_REQUEST_GEO_CONFIG (Payload)
GeoConfigResponse → MessageKt.URI_RESPONSE_GEO_CONFIG (Payload)
GlassesBoxStateRequest → MessageKt.URI_REQUEST_GLASSES_BOX_STATE (Payload)
GlassesBoxStateResponse → MessageKt.URI_RESPONSE_GLASSES_BOX_STATE (Payload)
GyroCalibrationResponse → MessageKt.URI_RESPONSE_GYRO_CAL (其他)
GyroCalibrationStatusRequest → MessageKt.URI_REQUEST_GYRO_CAL_STATUS (其他)
HaveMFIChipRequest → MessageKt.URI_REQUEST_HAVE_MFI_CHIP (Payload)
HaveMFIChipResponse → MessageKt.URI_RESPONSE_HAVE_MFI_CHIP (其他)
HeartbeatRequest → MessageKt.URI_REQUEST_HEARTBEAT (Payload)
HeartbeatResponse → MessageKt.URI_RESPONSE_HEARTBEAT (Payload)
InputInteractConfigRequest → MessageKt.URI_REQUEST_INPUT_INTERACT_CONFIG (Payload)
InputInteractConfigResponse → MessageKt.URI_RESPONSE_INPUT_INTERACT_CONFIG (Payload)
KeyboardResponse → MessageKt.URI_RESPONSE_KEYBOARD (Payload)
LocationHeartbeatResponse → MessageKt.URI_RESPONSE_NAV_HEARTBEAT (其他)
LocationResponse → MessageKt.URI_RESPONSE_LOCATION (其他)
LogcatIndRequest → MessageKt.URI_REQUEST_LOGCAT_IND (Payload)
LogcatRequest → MessageKt.URI_REQUEST_LOGCAT (Payload)
LogcatResponse → MessageKt.URI_RESPONSE_LOGCAT (其他)
MarsDeviceInfoRequest → MessageKt.URI_REQUEST_DEVICE_INFO (Payload)
MarsDeviceInfoResponse → MessageKt.URI_RESPONSE_DEVICE_INFO (Payload)
MarsFinishTaskResponse → MessageKt.URL_RESPONSE_FINISH_TASK (Payload)
MarsGetAIPlanRequest → MessageKt.URI_REQUEST_SWITCH_AI_AUDIO_PLAN (Payload)
MarsGetRayNeoAIRequest → MessageKt.URI_REQUEST_SWITCH_RAY_NEO_AI (Payload)
MarsGetRayNeoDJRequest → MessageKt.URI_PUSH_RAYNEO_DJ_SWITCH (Payload)
MarsGetRayNeoDJResponse → MessageKt.URI_PULL_RAYNEO_DJ_SWITCH (Payload)
MarsGetUserInfoResponse → MessageKt.URI_PULL_USER_INFO (Payload)
MarsLogSendTaskResponse → MessageKt.URI_RESPONSE_LOG_SEND_TASK (Payload)
MarsLogTaskProgressRequest → MessageKt.URI_REQUEST_LOG_TASK_PROGRESS (Payload)
MarsRadioStationPlayResponse → MessageKt.URI_RESPONSE_RADIO_STATION_PLAY (Payload)
MarsRadioStationPreferenceResponse → MessageKt.URI_RESPONSE_RADIO_STATION_PREFERENCE (Payload)
MarsRadioStationStateRequest → MessageKt.URI_REQUEST_RADIO_STATION_STATE (Payload)
MarsRadioStationStateResponse → MessageKt.URI_RESPONSE_RADIO_STATION_STATE (Payload)
MarsSetAIAudioPlanResponse → MessageKt.URI_RESPONSE_SWITCH_AI_AUDIO_PLAN (Payload)
MarsSetRayNeoAIResponse → MessageKt.URI_RESPONSE_SWITCH_RAY_NEO_AI (Payload)
MarsSetRayNeoDJResponse → MessageKt.URI_SWITCH_RAYNEO_DJ (Payload)
MarsSetStreamMusicControlResponse → MessageKt.URI_PULL_STREAM_MUSIC_CONTROL (Payload)
MarsStreamGetMusicTypeResponse → MessageKt.URI_RESPONSE_GET_STREAM_MUSIC_TYPE (Payload)
MarsStreamMusicTypeRequest → MessageKt.URI_REQUEST_STREAM_MUSIC_TYPE (Payload)
MarsStreamMusicsStateResponse → MessageKt.URI_RESPONSE_QQ_STREAM_MUSIC_STATE (Payload)
MarsStreamSetMusicTypeResponse → MessageKt.URI_RESPONSE_SET_STREAM_MUSIC_TYPE (Payload)
MarsStreamUserInfoRequest → MessageKt.URI_REQUEST_PUSH_USER_INFO (Payload)
MarsTranslationResponse → MessageKt.URI_RESPONSE_TRANSLATION_AUDIO_PARAM (Payload)
MixRecordRequest → MessageKt.URI_REQUEST_MIX_RECORD (其他)
MixRecordResponse → MessageKt.URI_RESPONSE_MIX_RECORD (其他)
MixShotRequest → MessageKt.URI_REQUEST_MIX_SHOT (其他)
MixShotResponse → MessageKt.URI_RESPONSE_MIX_SHOT (其他)
ModifyNameResponse → MessageKt.URI_RESPONSE_MODIFY_NAME (其他)
MultiDeviceRequest → MessageKt.URI_REQUEST_MULTI_DEVICE (Payload)
MultiDeviceResponse → MessageKt.URI_RESPONSE_MULTI_DEVICE (Payload)
NavHeartbeatRequest → MessageKt.URI_REQUEST_NAV_HEARTBEAT (其他)
NavRequest → MessageKt.URI_REQUEST_NAV (Payload)
NavRouteResponse → MessageKt.URI_RESPONSE_NAV_ROUTE (其他)
NetConfigRequest → MessageKt.URI_REQUEST_NET_CONFIG (Payload)
NetConfigResponse → MessageKt.URI_RESPONSE_NET_CONFIG (Payload)
NotesRequest → MessageKt.URI_REQUEST_NOTES (Payload)
NotesResponse → MessageKt.URI_RESPONSE_NOTES (Payload)
NotiAppsResponse → MessageKt.URI_RESPONSE_NOTI_APPS (其他)
OnlineTtsRequest → MessageKt.URI_REQUEST_ONLINE_TTS (Payload)
PhoneRecordStateRequest → MessageKt.URI_REQUEST_PHONE_RECORD_STATE (Payload)
PhoneRecordStateResponse → MessageKt.URI_RESPONSE_PHONE_RECORD_STATE (Payload)
PostNotificationResponse → MessageKt.URI_RESPONSE_POST_NOTIFICATION (其他)
PrompterFileRequest → MessageKt.URI_RESPONSE_PROMPTER_FILE (Payload)
PrompterFileResponse → MessageKt.URI_RESPONSE_PROMPTER_FILE (其他)
PrompterMessageRequest → ? (Payload)
PrompterMessageResponse → ? (Payload)
PullGeneralSettingsResponse → MessageKt.URI_RESPONSE_PULL_GENERAL_SETTINGS (其他)
PullGeneralStatusResponse → MessageKt.URI_RESPONSE_PULL_GENERAL_STATUS (其他)
QQMusicExecuteRequest → MessageKt.URI_REQUEST_QQ_MUSIC_EXECUTE (Payload)
QQMusicExecuteResponse → MessageKt.URI_RESPONSE_EXEC_QQ_MUSIC_RESULT (Payload)
RealBluetoothAddressRequest → MessageKt.URI_REQUEST_REAL_BLUETOOTH_ADDRESS (其他)
RemoveWifiResponse → MessageKt.URI_RESPONSE_REMOVE_WIFI (其他)
RingInfoRequest → MessageKt.URI_REQUEST_RING_INFO (其他)
RingSendQRCodeRequest → MessageKt.URI_REQUEST_SEND_QR_CODE (Payload)
ScanWifiResponse → MessageKt.URI_RESPONSE_SCAN_WIFI (其他)
ServerCloseConfirmRequest → MessageKt.URI_REQUEST_SERVER_CLOSE_CONFIRM (其他)
ServerCloseResponse → MessageKt.URI_RESPONSE_SERVER_CLOSE (其他)
SetLanguageResponse → MessageKt.URI_RESPONSE_SET_LANGUAGE (其他)
SetTimeResponse → MessageKt.URI_RESPONSE_SET_TIME (其他)
ShortcutsRequest → MessageKt.URI_REQUEST_SHORTCUTS (Payload)
ShortcutsResponse → MessageKt.URI_RESPONSE_SHORTCUTS (Payload)
SpeechEngineRequest → MessageKt.URI_REQUEST_SPEECH_ENGINE (Payload)
SpeechEngineResponse → MessageKt.URI_RESPONSE_SWITCH_SPEECH_ENGINE (Payload)
SpeechVqa4kRequest → MessageKt.URI_REQUEST_VQA4K (Payload)
StartFullLogResponse → MessageKt.URI_RESPONSE_START_FULL_LOG (其他)
StartFullLogSuccessRequest → MessageKt.URI_REQUEST_START_FULL_LOG_SUCCESS (其他)
StreamMusicAuthRequest → MessageKt.URI_REQUEST_STREAM_MUSIC_AUTH (Payload)
StreamMusicAuthResponse → MessageKt.URI_RESPONSE_STREAM_MUSIC_AUTH (Payload)
StreamMusicCollectionResponse → MessageKt.URI_RESPONSE_FAVORITE_STREAM_MUSIC (Payload)
StreamMusicStateRequest → MessageKt.URI_REQUEST_STREAM_MUSIC_STATE (Payload)
StreamMusicStateResponse → MessageKt.URI_RESPONSE_STREAM_MUSIC_STATE (Payload)
TetheringRequest → MessageKt.URI_REQUEST_TETHERING (Payload)
TetheringResponse → MessageKt.URI_RESPONSE_TETHERING (Payload)
ToggleWifiResponse → MessageKt.URI_RESPONSE_TOGGLE_WIFI (其他)
TouchRequest → MessageKt.URI_REQUEST_TOUCH (其他)
TouchResponse → MessageKt.URI_RESPONSE_TOUCH (其他)
UpdateGeneralStatusResponse → MessageKt.URI_RESPONSE_UPDATE_GENERAL_STATUS (其他)
UserGuideRequest → MessageKt.URI_REQUEST_USER_GUIDE (Payload)
UserGuideResponse → MessageKt.URI_RESPONSE_USER_GUIDE (Payload)
VQA4KResponse → MessageKt.URI_RESPONSE_SWITCH_VQA4K (Payload)
VerifyVersionRequest → MessageKt.URI_REQUEST_VERIFY_VERSION (Payload)
VerifyVersionResponse → MessageKt.URI_RESPONSE_VERIFY_VERSION (Payload)
VolumeSyncSwitchTipDialogRequest → MessageKt.URI_REQUEST_VOLUME_SYNC_SWITCH_TIP_DIALOG (其他)
WifiConnectStateRequest → MessageKt.URI_REQUEST_WIFI_CONNECT_STATE (其他)
WifiScanResultRequest → MessageKt.URI_REQUEST_SCAN_WIFI_RESULT (其他)
WindowScaleConfigRequest → MessageKt.URI_REQUEST_WINDOW_SCALE_CONFIG (Payload)
WindowScaleConfigResponse → MessageKt.URI_RESPONSE_WINDOW_SCALE_CONFIG (Payload)