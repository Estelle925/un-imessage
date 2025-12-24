package com.unimessage.handler.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.unimessage.entity.LogMsgDetail;
import com.unimessage.entity.SysChannel;
import com.unimessage.entity.SysTemplate;
import com.unimessage.enums.ChannelType;
import com.unimessage.handler.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 钉钉消息发送处理器
 * 支持 Webhook 机器人模式
 *
 * @author 海明
 * @since 2025-12-04
 */
@Slf4j
@Component
public class DingTalkHandler implements ChannelHandler {

    private static final String KEY_WEBHOOK = "webhook";
    private static final String KEY_MSGTYPE = "msgtype";
    private static final String KEY_TEXT = "text";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_AT = "at";
    private static final String KEY_AT_MOBILES = "atMobiles";
    private static final String KEY_ERRCODE = "errcode";
    private static final String KEY_ERRMSG = "errmsg";
    private static final String MSG_TYPE_TEXT = "text";
    private static final int SUCCESS_CODE = 0;
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    @Override
    public boolean support(String channelType) {
        return ChannelType.DINGTALK.getCode().equals(channelType);
    }

    @Override
    public boolean send(SysChannel channel, SysTemplate template, LogMsgDetail msgDetail, Map<String, Object> params) {
        log.info("开始发送钉钉消息: recipient={}", msgDetail.getRecipient());

        try {
            JSONObject config = JSON.parseObject(channel.getConfigJson());
            String webhook = config.getString(KEY_WEBHOOK);

            if (webhook != null && !webhook.isEmpty()) {
                return sendByWebhook(webhook, template, msgDetail, params);
            }

            throw new UnsupportedOperationException("目前仅支持钉钉 Webhook 方式");

        } catch (Exception e) {
            log.error("钉钉消息发送异常", e);
            msgDetail.setErrorMsg(e.getMessage());
            return false;
        }
    }

    /**
     * 通过 Webhook 发送消息
     */
    private boolean sendByWebhook(String webhook, SysTemplate template, LogMsgDetail msgDetail,
                                  Map<String, Object> params) {
        try {
            String content = renderContent(template.getContent(), params);
            msgDetail.setContent(content);

            JSONObject request = new JSONObject();
            request.put(KEY_MSGTYPE, MSG_TYPE_TEXT);
            JSONObject text = new JSONObject();
            text.put(KEY_CONTENT, content);
            request.put(KEY_TEXT, text);

            if (isPhoneNumber(msgDetail.getRecipient())) {
                JSONObject at = new JSONObject();
                at.put(KEY_AT_MOBILES, java.util.Collections.singletonList(msgDetail.getRecipient()));
                request.put(KEY_AT, at);
            }

            String result = cn.hutool.http.HttpUtil.post(webhook, request.toJSONString());
            log.info("钉钉Webhook响应: {}", result);

            JSONObject res = JSON.parseObject(result);
            if (res.getIntValue(KEY_ERRCODE) == SUCCESS_CODE) {
                msgDetail.setThirdPartyMsgId("Ding_" + System.currentTimeMillis());
                return true;
            } else {
                msgDetail.setErrorMsg(res.getString(KEY_ERRMSG));
                return false;
            }
        } catch (Exception e) {
            log.error("钉钉Webhook发送失败", e);
            msgDetail.setErrorMsg(e.getMessage());
            return false;
        }
    }

    /**
     * 判断是否为手机号格式
     */
    private boolean isPhoneNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches(PHONE_REGEX);
    }

    /**
     * 渲染模板内容
     */
    private String renderContent(String template, Map<String, Object> params) {
        String content = template;
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = "${" + entry.getKey() + "}";
                if (content.contains(key)) {
                    content = content.replace(key, String.valueOf(entry.getValue()));
                }
            }
        }
        return content;
    }
}
