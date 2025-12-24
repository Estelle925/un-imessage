package com.unimessage.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 应用 DTO
 *
 * @author 海明
 */
@Data
public class SysAppDto implements Serializable {
    private Long id;
    private String appName;
    private String appCode;
    private String owner;
    private String description;
    private Integer status;
}
