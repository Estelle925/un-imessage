package com.unimessage.mq.producer;

import com.alibaba.fastjson2.JSON;
import com.unimessage.dto.MqMessage;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * RocketMQ 消息生产者
 *
 * @author 海明
 * @since 2025-12-08
 */
@Component
@ConditionalOnProperty(name = "un-imessage.mq.type", havingValue = "rocketmq")
public class RocketMqProducer implements MqProducer {

    private static final String TOPIC = "un-imessage-send-topic";

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void send(MqMessage message) {
        rocketMQTemplate.convertAndSend(TOPIC, JSON.toJSONString(message));
    }
}
