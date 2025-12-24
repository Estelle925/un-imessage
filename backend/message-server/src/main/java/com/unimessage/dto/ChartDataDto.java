package com.unimessage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 图表数据 DTO
 *
 * @author 海明
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataDto implements Serializable {
    /**
     * 名称 (X轴/类别)
     */
    private String name;

    /**
     * 值 (Y轴/数量)
     */
    private Long value;
}
