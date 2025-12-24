package com.unimessage.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 渠道 DTO
 *
 * @author 海明
 */
@Data
public class SysChannelDto implements Serializable {
    private Long id;
    private String name;
    private String type;
    private String provider;
    private String configJson;
    private Integer status;
}
