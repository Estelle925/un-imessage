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
 * 飞书消息推送处理器
 * 支持 Webhook 机器人模式和应用消息模式
 *
 * @author 海明
 * @since 2025-12-04
 */
@Slf4j
@Component
public class FeishuHandler implements ChannelHandler {

    private static final String KEY_WEBHOOK = "webhook";
    private static final String KEY_APP_ID = "appId";
    private static final String KEY_APP_SECRET = "appSecret";
    private static final String KEY_TOKEN_URL = "tokenUrl";
    private static final String KEY_SEND_URL = "sendUrl";
    private static final String DEFAULT_TOKEN_URL = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal";
    private static final String DEFAULT_SEND_URL = "https://open.feishu.cn/open-apis/im/v1/messages?receive_id_type=open_id";
    private static final String KEY_MSG_TYPE = "msg_type";
    private static final String KEY_TEXT = "text";
    private static final String KEY_CONTENT = "content";
    private static final String MSG_TYPE_TEXT = "text";
    private static final String KEY_TENANT_ACCESS_TOKEN = "tenant_access_token";
    private static final String KEY_RECEIVE_ID = "receive_id";
    private static final String KEY_CODE = "code";
    private static final String KEY_STATUS_CODE = "StatusCode";
    private static final String KEY_MSG = "msg";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DATA = "data";
    private static final String KEY_MESSAGE_ID = "message_id";
    private static final int SUCCESS_CODE = 0;

    @Override
    public boolean support(String channelType) {
        return ChannelType.FEISHU.getCode().equals(channelType);
    }

    @Override
    public boolean send(SysChannel channel, SysTemplate template, LogMsgDetail msgDetail, Map<String, Object> params) {
        log.info("开始发送飞书消息: recipient={}", msgDetail.getRecipient());

        try {
            JSONObject config = JSON.parseObject(channel.getConfigJson());
            String webhook = config.getString(KEY_WEBHOOK);

            if (webhook != null && !webhook.isEmpty()) {
                return sendByWebhook(webhook, template, msgDetail, params);
            }

            String appId = config.getString(KEY_APP_ID);
            String appSecret = config.getString(KEY_APP_SECRET);
            if (appId != null && appSecret != null) {
                return sendByApp(config, appId, appSecret, template, msgDetail, params);
            }

            throw new IllegalArgumentException("飞书配置错误: 需要配置 webhook 或 appId/appSecret");

        } catch (Exception e) {
            log.error("飞书消息发送异常", e);
            msgDetail.setErrorMsg(e.getMessage());
            return false;
        }
    }

    /**
     * Webhook 机器人模式发送
     */
    private boolean sendByWebhook(String webhook, SysTemplate template, LogMsgDetail msgDetail,
                                  Map<String, Object> params) {
        try {
            String content = renderContent(template.getContent(), params);
            msgDetail.setContent(content);

            JSONObject request = new JSONObject();
            request.put(KEY_MSG_TYPE, MSG_TYPE_TEXT);
            JSONObject textContent = new JSONObject();
            textContent.put(KEY_TEXT, content);
            request.put(KEY_CONTENT, textContent);

            String result = cn.hutool.http.HttpUtil.post(webhook, request.toJSONString());
            log.info("飞书Webhook响应: {}", result);

            JSONObject res = JSON.parseObject(result);
            int code = res.getIntValue(KEY_CODE);
            int statusCode = res.getIntValue(KEY_STATUS_CODE);

            if (code == SUCCESS_CODE || statusCode == SUCCESS_CODE) {
                msgDetail.setThirdPartyMsgId("Feishu_" + System.currentTimeMillis());
                return true;
            } else {
                String errorMsg = res.getString(KEY_MSG);
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = res.getString(KEY_MESSAGE);
                }
                msgDetail.setErrorMsg(errorMsg != null ? errorMsg : "飞书返回未知错误");
                log.error("飞书Webhook发送失败: {}", errorMsg);
                return false;
            }
        } catch (Exception e) {
            log.error("飞书Webhook发送异常", e);
            msgDetail.setErrorMsg(e.getMessage());
            return false;
        }
    }

    /**
     * 应用消息模式发送（使用官方SDK）
     */
    private boolean sendByApp(JSONObject config, String appId, String appSecret, SysTemplate template,
                              LogMsgDetail msgDetail, Map<String, Object> params) {
        try {

            String tokenUrl = config.getString(KEY_TOKEN_URL);
            if (tokenUrl == null || tokenUrl.isEmpty()) {
                tokenUrl = DEFAULT_TOKEN_URL;
            }

            JSONObject tokenReq = new JSONObject();
            tokenReq.put("app_id", appId);
            tokenReq.put("app_secret", appSecret);

            String tokenRes = cn.hutool.http.HttpUtil.post(tokenUrl, tokenReq.toJSONString());
            JSONObject tokenJson = JSON.parseObject(tokenRes);

            if (tokenJson.getIntValue(KEY_CODE) != SUCCESS_CODE) {
                throw new RuntimeException("获取飞书Token失败: " + tokenJson.getString(KEY_MSG));
            }

            String accessToken = tokenJson.getString(KEY_TENANT_ACCESS_TOKEN);

            String content = renderContent(template.getContent(), params);
            msgDetail.setContent(content);

            String sendUrl = config.getString(KEY_SEND_URL);
            if (sendUrl == null || sendUrl.isEmpty()) {
                sendUrl = DEFAULT_SEND_URL;
            }

            JSONObject sendReq = new JSONObject();
            sendReq.put(KEY_RECEIVE_ID, msgDetail.getRecipient());
            sendReq.put(KEY_MSG_TYPE, MSG_TYPE_TEXT);
            sendReq.put(KEY_CONTENT, JSON.toJSONString(Map.of(KEY_TEXT, content)));

            String sendRes = cn.hutool.http.HttpRequest.post(sendUrl)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .body(sendReq.toJSONString())
                    .execute()
                    .body();

            log.info("飞书应用消息响应: {}", sendRes);
            JSONObject sendJson = JSON.parseObject(sendRes);

            if (sendJson.getIntValue(KEY_CODE) == SUCCESS_CODE) {
                msgDetail.setThirdPartyMsgId(sendJson.getJSONObject(KEY_DATA).getString(KEY_MESSAGE_ID));
                return true;
            } else {
                msgDetail.setErrorMsg(sendJson.getString(KEY_MSG));
                return false;
            }

        } catch (Exception e) {
            throw new RuntimeException("飞书应用消息发送失败", e);
        }
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
