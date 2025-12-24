package com.unimessage.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unimessage.common.Result;
import com.unimessage.dto.SysUserDto;
import com.unimessage.dto.SysUserRespDto;
import com.unimessage.entity.SysUser;
import com.unimessage.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 系统用户管理控制器
 *
 * @author 海明
 */
@RestController
@RequestMapping("/api/v1/user")
public class SysUserController {

    @Resource
    private SysUserMapper userMapper;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public Result<IPage<SysUserRespDto>> page(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              @RequestParam(required = false) String username) {
        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        wrapper.orderByDesc(SysUser::getCreatedAt);
        Page<SysUser> sysUserPage = userMapper.selectPage(page, wrapper);

        IPage<SysUserRespDto> resultPage = sysUserPage.convert(this::convertToDto);
        return Result.success(resultPage);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<SysUserRespDto> create(@RequestBody SysUserDto userDto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userDto, user);

        // 检查用户名是否存在
        LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
        query.eq(SysUser::getUsername, user.getUsername());
        if (userMapper.selectCount(query) > 0) {
            return Result.fail("用户名已存在");
        }

        user.setCreatedAt(LocalDateTime.now());
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        // 密码加密
        if (user.getPassword() != null) {
            user.setPassword(BCrypt.hashpw(user.getPassword()));
        }
        userMapper.insert(user);
        return Result.success(convertToDto(user));
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<SysUserRespDto> update(@PathVariable Long id, @RequestBody SysUserDto userDto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userDto, user);
        user.setId(id);

        // 密码不在这里更新，有单独的接口
        user.setPassword(null);

        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return Result.success(convertToDto(userMapper.selectById(id)));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (id == 1L) {
            return Result.fail("超级管理员无法删除");
        }
        userMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody SysUserDto userDto) {
        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            return Result.fail("密码不能为空");
        }
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(BCrypt.hashpw(userDto.getPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return Result.success();
    }

    private SysUserRespDto convertToDto(SysUser user) {
        if (user == null) {
            return null;
        }
        SysUserRespDto dto = new SysUserRespDto();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
