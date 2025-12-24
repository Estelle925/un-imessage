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
 * 邮件发送处理器
 * 基于 Hutool MailUtil 实现 SMTP 邮件发送
 *
 * @author 海明
 * @since 2025-12-04
 */
@Slf4j
@Component
public class EmailHandler implements ChannelHandler {

    @Override
    public boolean support(String channelType) {
        return ChannelType.EMAIL.getCode().equals(channelType);
    }

    @Override
    public boolean send(SysChannel channel, SysTemplate template, LogMsgDetail msgDetail, Map<String, Object> params) {
        log.info("开始发送邮件: recipient={}", msgDetail.getRecipient());

        try {
            JSONObject config = JSON.parseObject(channel.getConfigJson());
            String host = config.getString("host");
            Integer port = config.getInteger("port");
            String username = config.getString("username");
            String password = config.getString("password");
            Boolean ssl = config.getBoolean("ssl");

            validateConfig(host, port, username, password);

            cn.hutool.extra.mail.MailAccount account = new cn.hutool.extra.mail.MailAccount();
            account.setHost(host);
            account.setPort(port);
            account.setAuth(true);
            account.setFrom(username);
            account.setUser(username);
            account.setPass(password);
            account.setSslEnable(ssl != null && ssl);

            String content = renderContent(template.getContent(), params);
            msgDetail.setContent(content);

            String msgId = cn.hutool.extra.mail.MailUtil.send(account,
                    msgDetail.getRecipient(),
                    template.getTitle(),
                    content,
                    false);

            log.info("邮件发送成功: msgId={}", msgId);
            msgDetail.setThirdPartyMsgId(msgId);
            return true;

        } catch (Exception e) {
            log.error("邮件发送异常", e);
            msgDetail.setErrorMsg(e.getMessage());
            return false;
        }
    }

    /**
     * 校验配置参数
     */
    private void validateConfig(String host, Integer port, String username, String password) {
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("邮件配置缺失: host 为空");
        }
        if (port == null) {
            throw new IllegalArgumentException("邮件配置缺失: port 为空");
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("邮件配置缺失: username 为空");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("邮件配置缺失: password 为空");
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
