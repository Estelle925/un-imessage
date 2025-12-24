package com.unimessage.handler;

import com.unimessage.entity.LogMsgDetail;
import com.unimessage.entity.SysChannel;
import com.unimessage.entity.SysTemplate;
import com.unimessage.enums.ChannelType;

import java.util.Map;

/**
 * 渠道处理策略接口
 *
 * @author 海明
 * @since 2025-12-04
 */
public interface ChannelHandler {

    /**
     * 是否支持该渠道类型
     *
     * @param channelType 渠道类型代码 (SMS, EMAIL, WECHAT_OFFICIAL, etc.)
     * @return true if supported
     */
    boolean support(String channelType);

    /**
     * 是否支持该渠道类型
     *
     * @param channelType 渠道类型枚举
     * @return true if supported
     */
    default boolean support(ChannelType channelType) {
        return channelType != null && support(channelType.getCode());
    }

    /**
     * 执行发送逻辑
     *
     * @param channel   渠道配置信息
     * @param template  模板信息
     * @param msgDetail 消息详情 (包含接收者)
     * @param params    业务参数
     * @return true 发送成功, false 发送失败
     */
    boolean send(SysChannel channel, SysTemplate template, LogMsgDetail msgDetail, Map<String, Object> params);
}
