package com.unimessage.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应 DTO
 *
 * @author 海明
 */
@Data
public class LoginRespDto implements Serializable {
    /**
     * Token 值
     */
    private String token;

    /**
     * 用户信息
     */
    private SysUserRespDto user;
}
