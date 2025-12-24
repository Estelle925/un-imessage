package com.unimessage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 接入应用表
 *
 * @author 海明
 */
@Data
@TableName("sys_app")
public class SysApp implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 应用名称 (如: 订单系统)
     */
    private String appName;

    /**
     * 应用编码 (唯一标识)
     */
    private String appCode;

    /**
     * 接口鉴权Key
     */
    private String appKey;

    /**
     * 接口鉴权Secret
     */
    private String appSecret;

    /**
     * 状态: 1启用 0禁用
     */
    private Integer status;

    /**
     * 负责人
     */
    private String owner;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
