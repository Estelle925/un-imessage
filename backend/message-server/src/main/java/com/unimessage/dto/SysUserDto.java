package com.unimessage.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 海明
 */
@Data
public class SysUserDto implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private Integer status;
}
