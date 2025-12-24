package com.unimessage.mq.listener;

import com.alibaba.fastjson2.JSON;
import com.unimessage.dto.MqMessage;
import com.unimessage.service.MessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka 消息监听器
 *
 * @author 海明
 * @since 2025-12-08
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "un-imessage.mq.type", havingValue = "kafka")
public class KafkaMqListener {

    private static final String TOPIC = "un-imessage-send-topic";
    private static final String GROUP_ID = "un-imessage-group";

    @Resource
    private MessageService messageService;

    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            String value = record.value();
            if (value != null) {
                MqMessage message = JSON.parseObject(value, MqMessage.class);
                messageService.processBatch(message);
            }
        } catch (Exception e) {
            log.error("Error processing Kafka message", e);
        }
    }
}
