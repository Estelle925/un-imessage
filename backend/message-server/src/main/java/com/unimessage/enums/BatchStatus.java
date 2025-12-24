package com.unimessage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 批次状态枚举
 *
 * @author 海明
 * @since 2025-12-04
 */
@Getter
@AllArgsConstructor
public enum BatchStatus {

    /**
     * 处理中
     */
    PENDING(0, "处理中"),

    /**
     * 全部成功
     */
    SUCCESS(10, "全部成功"),

    /**
     * 部分成功
     */
    PARTIAL_SUCCESS(20, "部分成功"),

    /**
     * 全部失败
     */
    FAIL(30, "全部失败");

    private final Integer code;
    private final String desc;
}
