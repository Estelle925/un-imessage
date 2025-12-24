package com.unimessage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author 海明
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private String batchNo;

    public static SendResponse success(String batchNo) {
        return new SendResponse(true, "提交成功", batchNo);
    }

    public static SendResponse fail(String message) {
        return new SendResponse(false, message, null);
    }
}
