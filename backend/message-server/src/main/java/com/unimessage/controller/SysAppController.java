package com.unimessage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unimessage.common.Result;
import com.unimessage.dto.SysAppDto;
import com.unimessage.dto.SysAppRespDto;
import com.unimessage.entity.SysApp;
import com.unimessage.mapper.SysAppMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 应用管理控制器
 *
 * @author 海明
 * @since 2025-12-04
 */
@RestController
@RequestMapping("/api/v1/app")
public class SysAppController {

    @Resource
    private SysAppMapper appMapper;

    /**
     * 分页查询应用列表
     */
    @GetMapping("/page")
    public Result<IPage<SysAppRespDto>> page(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(required = false) String appName) {
        Page<SysApp> page = new Page<>(current, size);
        LambdaQueryWrapper<SysApp> wrapper = new LambdaQueryWrapper<>();
        if (appName != null && !appName.isEmpty()) {
            wrapper.like(SysApp::getAppName, appName);
        }
        wrapper.orderByDesc(SysApp::getCreatedAt);
        Page<SysApp> sysAppPage = appMapper.selectPage(page, wrapper);

        IPage<SysAppRespDto> resultPage = sysAppPage.convert(this::convertToDto);
        return Result.success(resultPage);
    }

    /**
     * 查询所有应用
     */
    @GetMapping("/list")
    public Result<List<SysAppRespDto>> list() {
        LambdaQueryWrapper<SysApp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysApp::getStatus, 1);
        wrapper.orderByDesc(SysApp::getCreatedAt);
        List<SysApp> list = appMapper.selectList(wrapper);
        return Result.success(list.stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    /**
     * 根据ID查询应用
     */
    @GetMapping("/{id}")
    public Result<SysAppRespDto> getById(@PathVariable Long id) {
        return Result.success(convertToDto(appMapper.selectById(id)));
    }

    /**
     * 创建应用
     */
    @PostMapping
    public Result<SysAppRespDto> create(@RequestBody SysAppDto appDto) {
        SysApp app = new SysApp();
        BeanUtils.copyProperties(appDto, app);
        //随机生成16位字符串
        String appCode = UUID.randomUUID().toString().replace("-", "");
        appCode = appCode.toUpperCase().substring(0, 16);
        app.setAppCode(appCode);

        // 生成 Key Secret
        app.setAppKey(UUID.randomUUID().toString().replace("-", ""));
        app.setAppSecret(UUID.randomUUID().toString().replace("-", ""));

        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());
        if (app.getStatus() == null) {
            app.setStatus(1);
        }
        appMapper.insert(app);
        return Result.success(convertToDto(app));
    }

    /**
     * 更新应用
     */
    @PutMapping("/{id}")
    public Result<SysAppRespDto> update(@PathVariable Long id, @RequestBody SysAppDto appDto) {
        SysApp app = new SysApp();
        BeanUtils.copyProperties(appDto, app);
        app.setId(id);
        app.setUpdatedAt(LocalDateTime.now());
        appMapper.updateById(app);
        return Result.success(convertToDto(appMapper.selectById(id)));
    }

    /**
     * 删除应用
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        appMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 启用/禁用应用
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysApp app = new SysApp();
        app.setId(id);
        app.setStatus(status);
        app.setUpdatedAt(LocalDateTime.now());
        appMapper.updateById(app);
        return Result.success();
    }

    /**
     * 重置应用密钥
     */
    @PostMapping("/{id}/reset-secret")
    public Result<SysAppRespDto> resetSecret(@PathVariable Long id) {
        SysApp app = appMapper.selectById(id);
        if (app != null) {
            String newSecret = UUID.randomUUID().toString().replace("-", "");
            app.setAppSecret(newSecret);
            app.setUpdatedAt(LocalDateTime.now());
            appMapper.updateById(app);
        }
        return Result.success(convertToDto(app));
    }

    private SysAppRespDto convertToDto(SysApp app) {
        if (app == null) {
            return null;
        }
        SysAppRespDto dto = new SysAppRespDto();
        BeanUtils.copyProperties(app, dto);
        return dto;
    }
}
