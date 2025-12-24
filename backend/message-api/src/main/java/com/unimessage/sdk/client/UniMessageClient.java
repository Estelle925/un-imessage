package com.unimessage.sdk.client;

import com.unimessage.dto.SendRequest;
import com.unimessage.dto.SendResponse;

/**
 * UniMessage 客户端接口
 *
 * @author 海明
 * @since 2025-12-08
 */
public interface UniMessageClient {

    /**
     * 发送消息
     *
     * @param request 发送请求
     * @return 发送响应
     */
    SendResponse send(SendRequest request);
}
