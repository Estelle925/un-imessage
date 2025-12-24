package com.unimessage.handler;

import com.unimessage.enums.ChannelType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 渠道处理器工厂
 * 负责管理和分发不同类型的渠道处理器
 *
 * @author 海明
 * @since 2025-12-04
 */
@Component
public class ChannelHandlerFactory {

    private final Map<String, ChannelHandler> handlerMap = new ConcurrentHashMap<>();

    @Autowired
    public ChannelHandlerFactory(List<ChannelHandler> handlers) {
        for (ChannelHandler handler : handlers) {
            for (ChannelType type : ChannelType.values()) {
                if (handler.support(type.getCode())) {
                    handlerMap.put(type.getCode(), handler);
                    break;
                }
            }
        }
    }

    /**
     * 根据渠道类型获取处理器
     *
     * @param channelType 渠道类型
     * @return 对应的处理器，如果不存在则返回 null
     */
    public ChannelHandler getHandler(String channelType) {
        return handlerMap.get(channelType);
    }

    /**
     * 根据渠道类型枚举获取处理器
     *
     * @param channelType 渠道类型枚举
     * @return 对应的处理器，如果不存在则返回 null
     */
    public ChannelHandler getHandler(ChannelType channelType) {
        return channelType != null ? handlerMap.get(channelType.getCode()) : null;
    }

    /**
     * 注册新的渠道处理器
     *
     * @param channelType 渠道类型
     * @param handler     处理器实例
     */
    public void register(String channelType, ChannelHandler handler) {
        handlerMap.put(channelType, handler);
    }

    /**
     * 注册新的渠道处理器
     *
     * @param channelType 渠道类型枚举
     * @param handler     处理器实例
     */
    public void register(ChannelType channelType, ChannelHandler handler) {
        if (channelType != null) {
            handlerMap.put(channelType.getCode(), handler);
        }
    }
}
