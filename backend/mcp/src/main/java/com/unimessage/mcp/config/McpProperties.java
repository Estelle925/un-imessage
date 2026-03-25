package com.unimessage.mcp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hm
 */
@ConfigurationProperties(prefix = "unimessage.mcp")
public class McpProperties {
    private String serverName = "unimessage-mcp";
    private String serverVersion = "0.1.0";
    private String protocolVersion = "2024-11-05";
    private String apiBaseUrl = "http://localhost:8079";
    private String appKey;
    private String appSecret;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
