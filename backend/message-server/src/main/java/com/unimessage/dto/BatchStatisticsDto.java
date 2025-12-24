package com.unimessage.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 批次统计 DTO
 *
 * @author 海明
 */
@Data
public class BatchStatisticsDto implements Serializable {
    private Long totalCount;
    private Long successCount;
    private Long failCount;
}
