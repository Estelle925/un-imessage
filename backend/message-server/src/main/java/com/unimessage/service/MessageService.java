package com.unimessage.service;

import com.unimessage.dto.MqMessage;
import com.unimessage.dto.SendRequest;
import com.unimessage.dto.SendResponse;

/**
 * 消息服务
 *
 * @author 海明
 * @since 2025-12-04
 */
public interface MessageService {
    /**
     * 发送消息
     *
     * @param request 发送请求
     * @return 发送结果
     */
    SendResponse send(SendRequest request);

    /**
     * 异步处理批次
     *
     * @param message MQ消息
     */
    void processBatch(MqMessage message);

    /**
     * 重试发送单条消息
     *
     * @param detailId 详情ID
     * @return 结果
     */
    boolean retry(Long detailId);
}
