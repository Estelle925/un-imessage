package com.unimessage.mq.producer;

import com.alibaba.fastjson2.JSON;
import com.unimessage.dto.MqMessage;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka MQ 生产者
 *
 * @author 海明
 */
@Component
@ConditionalOnProperty(name = "un-imessage.mq.type", havingValue = "kafka")
public class KafkaMqProducer implements MqProducer {

    private static final String TOPIC = "un-imessage-send-topic";

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void send(MqMessage message) {
        kafkaTemplate.send(TOPIC, JSON.toJSONString(message));
    }
}
