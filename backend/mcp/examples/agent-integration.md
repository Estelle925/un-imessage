# UniMessage MCP Agent 集成示例

## 1. 初始化握手

```bash
curl -X POST "http://localhost:8090/mcp" ^
  -H "Content-Type: application/json" ^
  -d "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"initialize\",\"params\":{\"clientInfo\":{\"name\":\"demo-agent\",\"version\":\"1.0.0\"},\"capabilities\":{},\"protocolVersion\":\"2024-11-05\"}}"
```

## 2. 获取工具列表

```bash
curl -X POST "http://localhost:8090/mcp" ^
  -H "Content-Type: application/json" ^
  -d "{\"jsonrpc\":\"2.0\",\"id\":2,\"method\":\"tools/list\",\"params\":{}}"
```

## 3. 调用 send_message

```bash
curl -X POST "http://localhost:8090/mcp" ^
  -H "Content-Type: application/json" ^
  -d "{\"jsonrpc\":\"2.0\",\"id\":3,\"method\":\"tools/call\",\"params\":{\"name\":\"send_message\",\"arguments\":{\"templateCode\":\"WELCOME_SMS\",\"recipients\":[\"13800138000\"],\"params\":{\"name\":\"Alice\",\"code\":\"123456\"},\"bizId\":\"demo-biz-001\"}}}"
```

## 4. 调用 create_short_url

```bash
curl -X POST "http://localhost:8090/mcp" ^
  -H "Content-Type: application/json" ^
  -d "{\"jsonrpc\":\"2.0\",\"id\":4,\"method\":\"tools/call\",\"params\":{\"name\":\"create_short_url\",\"arguments\":{\"url\":\"https://example.com/promotion?a=1\",\"ttl\":3600}}}"
```

## 5. Python Agent 调用示例

```python
import json
import requests

endpoint = "http://localhost:8090/mcp"
headers = {"Content-Type": "application/json"}

def rpc(method, params, req_id):
    payload = {
        "jsonrpc": "2.0",
        "id": req_id,
        "method": method,
        "params": params
    }
    response = requests.post(endpoint, headers=headers, json=payload, timeout=10)
    response.raise_for_status()
    return response.json()

print(json.dumps(rpc("initialize", {
    "clientInfo": {"name": "python-agent", "version": "1.0.0"},
    "capabilities": {},
    "protocolVersion": "2024-11-05"
}, 1), ensure_ascii=False, indent=2))

print(json.dumps(rpc("tools/list", {}, 2), ensure_ascii=False, indent=2))

print(json.dumps(rpc("tools/call", {
    "name": "send_message",
    "arguments": {
        "templateCode": "WELCOME_SMS",
        "recipients": ["13800138000"],
        "params": {"name": "Alice", "code": "123456"}
    }
}, 3), ensure_ascii=False, indent=2))
```
