package com.unimessage.mq.listener;

import com.alibaba.fastjson2.JSON;
import com.unimessage.dto.MqMessage;
import com.unimessage.service.MessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * RocketMQ 消息监听器
 *
 * @author 海明
 * @since 2025-12-08
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "un-imessage.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(topic = "un-imessage-send-topic", consumerGroup = "un-imessage-group")
public class RocketMqMessageListener implements RocketMQListener<String> {

    @Resource
    private MessageService messageService;

    @Override
    public void onMessage(String messageJson) {
        try {
            MqMessage message = JSON.parseObject(messageJson, MqMessage.class);
            messageService.processBatch(message);
        } catch (Exception e) {
            log.error("Error processing RocketMQ message", e);
        }
    }
}
