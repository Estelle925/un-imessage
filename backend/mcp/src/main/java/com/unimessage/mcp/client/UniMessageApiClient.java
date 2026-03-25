package com.unimessage.mcp.client;

import com.unimessage.mcp.config.McpProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * @author hm
 */
@Component
public class UniMessageApiClient {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient;

    public UniMessageApiClient(McpProperties properties) {
        String baseUrl = properties.getApiBaseUrl().replaceAll("/$", "");
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeaders(headers -> {
                    if (properties.getAppKey() != null && !properties.getAppKey().isBlank()) {
                        headers.set("X-App-Key", properties.getAppKey());
                    }
                    if (properties.getAppSecret() != null && !properties.getAppSecret().isBlank()) {
                        headers.set("X-App-Secret", properties.getAppSecret());
                    }
                })
                .build();
    }

    public Map<String, Object> sendMessage(Map<String, Object> request) {
        return restClient.post()
                .uri("/api/v1/message/send")
                .body(request)
                .retrieve()
                .body(MAP_TYPE);
    }

    public Map<String, Object> createShortUrl(Map<String, Object> request) {
        return restClient.post()
                .uri("/api/v1/short-url")
                .body(request)
                .retrieve()
                .body(MAP_TYPE);
    }
}
