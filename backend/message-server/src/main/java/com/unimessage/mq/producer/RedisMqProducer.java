package com.unimessage.mq.producer;

import com.alibaba.fastjson2.JSON;
import com.unimessage.dto.MqMessage;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis MQ 生产者
 *
 * @author 海明
 */
@Component
@ConditionalOnProperty(name = "un-imessage.mq.type", havingValue = "redis", matchIfMissing = true)
public class RedisMqProducer implements MqProducer {

    private static final String MQ_KEY = "un-imessage:send:queue";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void send(MqMessage message) {
        stringRedisTemplate.opsForList().leftPush(MQ_KEY, JSON.toJSONString(message));
    }
}
