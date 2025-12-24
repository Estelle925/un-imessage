package com.unimessage.controller;

import com.unimessage.dto.SendRequest;
import com.unimessage.dto.SendResponse;
import com.unimessage.service.MessageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息发送控制器
 *
 * @author 海明
 * @since 2025-12-04
 */
@RestController
@RequestMapping("/api/v1/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @PostMapping("/send")
    public SendResponse send(@RequestBody SendRequest request) {
        return messageService.send(request);
    }
}
