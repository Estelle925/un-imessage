package com.unimessage.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 接收者 DTO
 *
 * @author 海明
 */
@Data
public class SysRecipientDto implements Serializable {
    private Long id;
    private String name;
    private String mobile;
    private String email;
    private String openId;
    private String userId;
    private Integer status;
}