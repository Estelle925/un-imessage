package com.unimessage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 接收者-分组关联表
 *
 * @author 海明
 * @since 2025-12-11
 */
@Data
@TableName("sys_recipient_group_relation")
public class SysRecipientGroupRelation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分组ID
     */
    private Long groupId;

    /**
     * 接收者ID
     */
    private Long recipientId;
}
