package com.unimessage.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户响应 DTO
 *
 * @author 海明
 */
@Data
public class SysUserRespDto implements Serializable {
    private Long id;
    private String username;
    private String nickname;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
