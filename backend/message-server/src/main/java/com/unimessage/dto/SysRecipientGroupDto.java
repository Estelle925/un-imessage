package com.unimessage.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 接收者分组 DTO
 *
 * @author 海明
 */
@Data
public class SysRecipientGroupDto implements Serializable {
    private Long id;
    private String groupName;
    private String description;
    private Integer status;
    private List<Long> recipientIds;
}