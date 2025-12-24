package com.unimessage.handler.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.unimessage.entity.LogMsgDetail;
import com.unimessage.entity.SysChannel;
import com.unimessage.entity.SysTemplate;
import com.unimessage.enums.ChannelType;
import com.unimessage.handler.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 企业微信消息发送处理器
 * 支持应用消息发送，使用客户端缓存提升性能
 *
 * @author 海明
 * @since 2025-12-04
 */
@Slf4j
@Component
public class WechatWorkHandler implements ChannelHandler {

    private final Map<Long, WxCpService> clientCache = new ConcurrentHashMap<>();

    @Override
    public boolean support(String channelType) {
        return ChannelType.WECHAT_WORK.getCode().equals(channelType);
    }

    @Override
    public boolean send(SysChannel channel, SysTemplate template, LogMsgDetail msgDetail, Map<String, Object> params) {
        log.info("开始发送企业微信消息: recipient={}", msgDetail.getRecipient());

        try {
            WxCpService wxCpService = getService(channel);

            JSONObject config = JSON.parseObject(channel.getConfigJson());
            Integer agentId = config.getIntValue("agentId");

            String content = renderContent(template.getContent(), params);
            msgDetail.setContent(content);

            WxCpMessage message = WxCpMessage.TEXT()
                    .agentId(agentId)
                    .toUser(msgDetail.getRecipient())
                    .content(content)
                    .build();

            wxCpService.getMessageService().send(message);

            log.info("企业微信消息发送成功");
            msgDetail.setThirdPartyMsgId("WxWork_" + System.currentTimeMillis());
            return true;

        } catch (Exception e) {
            log.error("企业微信消息发送异常", e);
            msgDetail.setErrorMsg(e.getMessage());
            return false;
        }
    }

    /**
     * 获取或创建 WxCpService 实例（带缓存）
     */
    private WxCpService getService(SysChannel channel) {
        return clientCache.computeIfAbsent(channel.getId(), k -> {
            JSONObject config = JSON.parseObject(channel.getConfigJson());
            String corpId = config.getString("corpId");
            String corpSecret = config.getString("corpSecret");
            Integer agentId = config.getIntValue("agentId");

            if (corpId == null || corpId.isEmpty() || corpSecret == null || corpSecret.isEmpty() || agentId == null) {
                throw new IllegalArgumentException("企业微信配置缺失: corpId, corpSecret 或 agentId 为空");
            }

            WxCpDefaultConfigImpl cpConfig = new WxCpDefaultConfigImpl();
            cpConfig.setCorpId(corpId);
            cpConfig.setCorpSecret(corpSecret);
            cpConfig.setAgentId(agentId);
            WxCpService service = new WxCpServiceImpl();
            service.setWxCpConfigStorage(cpConfig);

            log.info("初始化企业微信 SDK 客户端: channelId={}, corpId={}", channel.getId(), corpId);
            return service;
        });
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
