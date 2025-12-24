package com.unimessage.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 仪表盘统计数据 DTO
 *
 * @author 海明
 */
@Data
public class DashboardStatsDto implements Serializable {
    /**
     * 应用总数
     */
    private Long appCount;

    /**
     * 消息总发送量
     */
    private Long msgCount;

    /**
     * 用户总数
     */
    private Long userCount;

    /**
     * 整体成功率
     */
    private Double successRate;

    /**
     * 每日发送趋势 (近7天)
     */
    private List<ChartDataDto> trend;

    /**
     * 渠道发送分布
     */
    private List<ChartDataDto> channelDist;

    /**
     * 发送状态分布
     */
    private List<ChartDataDto> statusDist;
}
