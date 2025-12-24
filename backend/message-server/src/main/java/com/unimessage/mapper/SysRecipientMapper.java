package com.unimessage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unimessage.entity.SysRecipient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 海明
 */
@Mapper
public interface SysRecipientMapper extends BaseMapper<SysRecipient> {

    /**
     * 根据分组ID查询接收者列表
     *
     * @param groupId 分组ID
     * @return 接收者列表
     */
    @Select("SELECT r.* FROM sys_recipient r " +
            "INNER JOIN sys_recipient_group_relation gr ON r.id = gr.recipient_id " +
            "WHERE gr.group_id = #{groupId} AND r.status = 1")
    List<SysRecipient> selectByGroupId(@Param("groupId") Long groupId);
}
