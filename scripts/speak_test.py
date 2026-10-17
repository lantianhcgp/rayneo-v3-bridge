#!/usr/bin/env python3
"""让眼镜开口 - 候选指令矩阵（ble.response.* 反向URI假设验证）
用法: python3 speak_test.py [token]   # 默认用 app prefs 里的 token
逐条发送候选,每条间隔3s,观察眼镜是否发声。候选基于:
- online_tts 方向考证(眼镜->手机请求) => 手机->眼镜说话走 ble.response.*
- Payload{cmd,data,useCompress} + {payload,id,uri} 信封
"""
import json, uuid, subprocess, sys, time

TOKEN = sys.argv[1] if len(sys.argv) > 1 else "c9e7f7b65533454e839d4c4c"
TEXT = "你好，我是桥接助手"
CANDIDATES = [
    ("ble.response.speech", "set", "text", TEXT),
    ("ble.response.prompter_message", "set", "text", TEXT),
    ("ble.response.speech", "play", "text", TEXT),
    ("ble.response.prompter_message", "speak", "content", TEXT),
    ("ble.response.prompter_message", "set", "message", TEXT),
]

def send(uri, cmd, key, text):
    msg = {"payload": {"cmd": cmd, "data": json.dumps({key: text}, ensure_ascii=False)},
           "id": str(uuid.uuid4()), "uri": uri}
    req = {"jsonrpc": "2.0", "id": 9, "method": "tools/call",
           "params": {"name": "ble_send_json", "arguments": {"json": json.dumps(msg, ensure_ascii=False)}}}
    open("/data/data/com.termux/files/home/_bat.json", "w").write(json.dumps(req, ensure_ascii=False))
    r = subprocess.run(["curl", "-s", "-m", "20", "-X", "POST", "http://127.0.0.1:8766/mcp",
                        "-H", "Authorization: Bearer " + TOKEN,
                        "-H", "Content-Type: application/json",
                        "-d", "@/data/data/com.termux/files/home/_bat.json"],
                       capture_output=True, text=True)
    print(f"[{'OK' if r.stdout else 'TIMEOUT'}] uri={uri} cmd={cmd} data={{{key}:...}}")

for i, c in enumerate(CANDIDATES, 1):
    print(f"--- 候选 {i}/{len(CANDIDATES)} ---")
    send(*c)
    time.sleep(3)
print("完毕。听到任何一候选说话,记下编号。")
