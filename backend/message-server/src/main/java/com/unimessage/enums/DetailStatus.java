package com.unimessage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息详情状态枚举
 *
 * @author 海明
 * @since 2025-12-04
 */
@Getter
@AllArgsConstructor
public enum DetailStatus {

    /**
     * 发送中
     */
    SENDING(10, "发送中"),

    /**
     * 发送成功
     */
    SUCCESS(20, "发送成功"),

    /**
     * 发送失败
     */
    FAIL(30, "发送失败");

    private final Integer code;
    private final String desc;

    public static DetailStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (DetailStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
