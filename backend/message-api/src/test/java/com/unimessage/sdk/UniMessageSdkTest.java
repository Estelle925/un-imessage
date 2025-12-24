package com.unimessage.sdk;

import com.unimessage.sdk.client.UniMessageClient;
import com.unimessage.sdk.config.UniMessageProperties;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = UniMessageSdkTest.TestConfig.class, properties = {
        "un-imessage.client.host=localhost",
        "un-imessage.client.port=8079",
        "un-imessage.client.app-key=aliyun_test_key_123",
        "un-imessage.client.app-secret=76b0d0a009954bfa8b309f411d81129d"
})
public class UniMessageSdkTest {

    @Resource
    private UniMessageClient client;

    @Resource
    private UniMessageProperties properties;

    @Test
    public void testAutoConfig() {
        Assertions.assertNotNull(client);
        Assertions.assertEquals("test", properties.getAppKey());
        Assertions.assertEquals("http://localhost:8079/api/v1/message", properties.getBaseUrl());
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
    }
}
