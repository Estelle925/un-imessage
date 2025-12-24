package com.unimessage.mq.producer;

import com.unimessage.dto.MqMessage;

/**
 * Message Queue Producer Interface
 *
 * @author 海明
 */
public interface MqProducer {
    /**
     * Send message to MQ
     *
     * @param message Message content
     */
    void send(MqMessage message);
}
