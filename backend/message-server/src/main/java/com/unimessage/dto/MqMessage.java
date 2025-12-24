package com.unimessage.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 海明
 */
@Data
public class MqMessage implements Serializable {
    private Long batchId;
    private SendRequest request;
    /**
     * 接收者名称映射 (identifier -> name)
     */
    private Map<String, String> recipientNames;

    public MqMessage() {
    }

    public MqMessage(Long batchId, SendRequest request) {
        this.batchId = batchId;
        this.request = request;
    }

    public MqMessage(Long batchId, SendRequest request, Map<String, String> recipientNames) {
        this.batchId = batchId;
        this.request = request;
        this.recipientNames = recipientNames;
    }
}
