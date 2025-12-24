package com.unimessage.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 模板 DTO
 *
 * @author 海明
 */
@Data
public class SysTemplateDto implements Serializable {
    private Long id;
    private String name;
    private String code;
    private Long channelId;
    private Integer msgType;
    private String thirdPartyId;
    private String title;
    private String content;
    private String variables;
    private String deduplicationConfig;
    private List<Long> recipientGroupIds;
    private List<Long> recipientIds;
    private Integer status;
}
