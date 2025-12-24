package com.unimessage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unimessage.common.Result;
import com.unimessage.dto.SysChannelDto;
import com.unimessage.entity.SysChannel;
import com.unimessage.mapper.SysChannelMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 渠道配置管理控制器
 *
 * @author 海明
 * @since 2025-12-04
 */
@RestController
@RequestMapping("/api/v1/channel")
public class SysChannelController {

    @Resource
    private SysChannelMapper channelMapper;

    /**
     * 分页查询渠道列表
     */
    @GetMapping("/page")
    public Result<IPage<SysChannel>> page(@RequestParam(defaultValue = "1") Integer current,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) String type,
                                          @RequestParam(required = false) String name) {
        Page<SysChannel> page = new Page<>(current, size);
        LambdaQueryWrapper<SysChannel> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(SysChannel::getType, type);
        }
        if (name != null && !name.isEmpty()) {
            wrapper.like(SysChannel::getName, name);
        }
        wrapper.orderByDesc(SysChannel::getCreatedAt);
        return Result.success(channelMapper.selectPage(page, wrapper));
    }

    /**
     * 查询所有可用渠道
     */
    @GetMapping("/list")
    public Result<List<SysChannel>> list(@RequestParam(required = false) String type) {
        LambdaQueryWrapper<SysChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysChannel::getStatus, 1);
        if (type != null && !type.isEmpty()) {
            wrapper.eq(SysChannel::getType, type);
        }
        wrapper.orderByDesc(SysChannel::getCreatedAt);
        return Result.success(channelMapper.selectList(wrapper));
    }

    /**
     * 根据ID查询渠道
     */
    @GetMapping("/{id}")
    public Result<SysChannel> getById(@PathVariable Long id) {
        return Result.success(channelMapper.selectById(id));
    }

    /**
     * 创建渠道
     */
    @PostMapping
    public Result<SysChannel> create(@RequestBody SysChannelDto channelDto) {
        SysChannel channel = new SysChannel();
        BeanUtils.copyProperties(channelDto, channel);
        channel.setCreatedAt(LocalDateTime.now());
        if (channel.getStatus() == null) {
            channel.setStatus(1);
        }
        channelMapper.insert(channel);
        return Result.success(channel);
    }

    /**
     * 更新渠道
     */
    @PutMapping("/{id}")
    public Result<SysChannel> update(@PathVariable Long id, @RequestBody SysChannelDto channelDto) {
        SysChannel channel = new SysChannel();
        BeanUtils.copyProperties(channelDto, channel);
        channel.setId(id);
        channelMapper.updateById(channel);
        return Result.success(channelMapper.selectById(id));
    }

    /**
     * 删除渠道
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        channelMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 启用/禁用渠道
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysChannel channel = new SysChannel();
        channel.setId(id);
        channel.setStatus(status);
        channelMapper.updateById(channel);
        return Result.success();
    }

    /**
     * 测试渠道连接
     */
    @PostMapping("/{id}/test")
    public Result<String> testConnection(@PathVariable Long id) {
        SysChannel channel = channelMapper.selectById(id);
        if (channel == null) {
            return Result.fail("渠道不存在");
        }
        return Result.success("测试功能待实现");
    }
}
