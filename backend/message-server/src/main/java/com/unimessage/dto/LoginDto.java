package com.unimessage.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 海明
 */
@Data
public class LoginDto implements Serializable {
    private String username;
    private String password;
}
