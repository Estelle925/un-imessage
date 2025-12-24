package com.unimessage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unimessage.dto.ChartDataDto;
import com.unimessage.entity.LogMsgBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 批次发送日志 Mapper
 *
 * @author 海明
 * @since 2025-12-04
 */
@Mapper
public interface LogMsgBatchMapper extends BaseMapper<LogMsgBatch> {

    /**
     * 更新批次统计信息
     *
     * @param id           批次ID
     * @param successDelta 成功数增量
     * @param failDelta    失败数增量
     */
    @Update("update log_msg_batch set success_count = success_count + #{successDelta}, fail_count = fail_count + #{failDelta} where id = #{id}")
    void updateStats(@Param("id") Long id, @Param("successDelta") int successDelta, @Param("failDelta") int failDelta);

    /**
     * 获取渠道发送量分布
     *
     * @return 渠道发送量统计列表
     */
    @Select("SELECT IFNULL(channel_name, '未知渠道') as name, SUM(total_count) as value FROM log_msg_batch GROUP BY channel_name")
    List<ChartDataDto> getChannelDist();
}
