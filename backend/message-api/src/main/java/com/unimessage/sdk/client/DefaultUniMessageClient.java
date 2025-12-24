package com.unimessage.sdk.client;

import com.unimessage.dto.SendRequest;
import com.unimessage.dto.SendResponse;
import com.unimessage.sdk.config.UniMessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * UniMessage 客户端默认实现
 *
 * @author 海明
 * @since 2025-12-08
 */
@Slf4j
public class DefaultUniMessageClient implements UniMessageClient {

    private final UniMessageProperties properties;
    private final RestTemplate restTemplate;

    public DefaultUniMessageClient(UniMessageProperties properties) {
        this.properties = properties;
        this.restTemplate = createRestTemplate();
    }

    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getConnectTimeout());
        factory.setReadTimeout(properties.getReadTimeout());
        return new RestTemplate(factory);
    }

    @Override
    public SendResponse send(SendRequest request) {
        String url = properties.getBaseUrl() + "/send";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 鉴权头
        if (properties.getAppKey() != null) {
            headers.add("X-App-Key", properties.getAppKey());
        }
        if (properties.getAppSecret() != null) {
            headers.add("X-App-Secret", properties.getAppSecret());
        }

        HttpEntity<SendRequest> entity = new HttpEntity<>(request, headers);

        int maxRetries = properties.getMaxRetries();
        long retryInterval = properties.getRetryInterval();
        int attempt = 0;
        Exception lastException = null;

        long startTime = System.currentTimeMillis();

        while (attempt <= maxRetries) {
            try {
                attempt++;
                SendResponse response = restTemplate.postForObject(url, entity, SendResponse.class);
                long duration = System.currentTimeMillis() - startTime;
                log.debug("UniMessage send success, duration={}ms, batchNo={}", duration, response != null ? response.getBatchNo() : "null");
                return response;
            } catch (RestClientException e) {
                lastException = e;
                log.warn("UniMessage send failed (attempt {}/{}), error: {}", attempt, maxRetries + 1, e.getMessage());
                if (attempt <= maxRetries) {
                    try {
                        // 简单退避策略
                        Thread.sleep(retryInterval * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during retry", ie);
                    }
                }
            }
        }

        log.error("UniMessage send failed after {} attempts", attempt, lastException);
        throw new RuntimeException("Failed to send message after " + attempt + " attempts", lastException);
    }
}
