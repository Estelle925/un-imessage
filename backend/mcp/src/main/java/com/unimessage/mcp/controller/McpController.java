package com.unimessage.mcp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimessage.mcp.client.UniMessageApiClient;
import com.unimessage.mcp.config.McpProperties;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class McpController {

    private final UniMessageApiClient apiClient;
    private final McpProperties properties;
    private final ObjectMapper objectMapper;

    public McpController(UniMessageApiClient apiClient, McpProperties properties, ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @PostMapping(path = "/mcp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> handle(@RequestBody Map<String, Object> request) {
        Object id = request.get("id");
        String jsonrpc = asString(request.get("jsonrpc"));
        String method = asString(request.get("method"));
        Map<String, Object> params = asMap(request.get("params"));

        if (!"2.0".equals(jsonrpc) || method == null || method.isBlank()) {
            return error(id, -32600, "Invalid Request");
        }

        try {
            return switch (method) {
                case "initialize" -> success(id, initializeResult());
                case "tools/list" -> success(id, toolsListResult());
                case "tools/call" -> success(id, callTool(params));
                case "notifications/initialized" -> notificationAck();
                default -> error(id, -32601, "Method not found");
            };
        } catch (IllegalArgumentException e) {
            return error(id, -32602, e.getMessage());
        } catch (Exception e) {
            return error(id, -32000, e.getMessage() == null ? "Server error" : e.getMessage());
        }
    }

    private Map<String, Object> initializeResult() {
        Map<String, Object> capabilities = Map.of(
                "tools", Map.of("listChanged", false)
        );
        Map<String, Object> serverInfo = Map.of(
                "name", properties.getServerName(),
                "version", properties.getServerVersion()
        );
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("protocolVersion", properties.getProtocolVersion());
        result.put("capabilities", capabilities);
        result.put("serverInfo", serverInfo);
        return result;
    }

    private Map<String, Object> toolsListResult() {
        Map<String, Object> sendTool = Map.of(
                "name", "send_message",
                "description", "发送 UniMessage 消息",
                "inputSchema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "templateCode", Map.of("type", "string"),
                                "recipients", Map.of("type", "array", "items", Map.of("type", "string")),
                                "params", Map.of("type", "object"),
                                "bizId", Map.of("type", "string")
                        ),
                        "required", List.of("templateCode")
                )
        );

        Map<String, Object> shortUrlTool = Map.of(
                "name", "create_short_url",
                "description", "创建 UniMessage 短链接",
                "inputSchema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "url", Map.of("type", "string", "format", "uri"),
                                "customCode", Map.of("type", "string"),
                                "ttl", Map.of("type", "integer", "minimum", 0)
                        ),
                        "required", List.of("url")
                )
        );

        return Map.of("tools", List.of(sendTool, shortUrlTool));
    }

    private Map<String, Object> callTool(Map<String, Object> params) {
        String name = requireNotBlank(asString(params.get("name")), "tools/call 缺少 name");
        Map<String, Object> arguments = asMap(params.get("arguments"));

        if ("send_message".equals(name)) {
            requireNotBlank(asString(arguments.get("templateCode")), "templateCode 不能为空");
            Map<String, Object> response = apiClient.sendMessage(arguments);
            return toolResult(response, false);
        }

        if ("create_short_url".equals(name)) {
            requireNotBlank(asString(arguments.get("url")), "url 不能为空");
            Map<String, Object> response = apiClient.createShortUrl(arguments);
            Object code = response.get("code");
            if (!(code instanceof Number number) || number.intValue() != 200) {
                return toolResult(response, true);
            }
            return toolResult(response.get("data"), false);
        }

        throw new IllegalArgumentException("未知工具: " + name);
    }

    private Map<String, Object> toolResult(Object body, boolean isError) {
        String text = toJson(body);
        Map<String, Object> content = Map.of("type", "text", "text", text);
        return Map.of(
                "content", List.of(content),
                "isError", isError
        );
    }

    private String toJson(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            return String.valueOf(body);
        }
    }

    private Map<String, Object> notificationAck() {
        return Map.of("jsonrpc", "2.0", "result", Map.of());
    }

    private Map<String, Object> success(Object id, Object result) {
        return Map.of(
                "jsonrpc", "2.0",
                "id", id,
                "result", result
        );
    }

    private Map<String, Object> error(Object id, int code, String message) {
        return Map.of(
                "jsonrpc", "2.0",
                "id", id,
                "error", Map.of(
                        "code", code,
                        "message", message
                )
        );
    }

    private String asString(Object value) {
        return value instanceof String s ? s : null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        return value instanceof Map<?, ?> map ? (Map<String, Object>) map : new LinkedHashMap<>();
    }

    private String requireNotBlank(String value, @NotBlank String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
