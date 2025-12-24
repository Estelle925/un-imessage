package com.unimessage.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unimessage.common.Result;
import com.unimessage.dto.LoginDto;
import com.unimessage.dto.LoginRespDto;
import com.unimessage.dto.SysUserRespDto;
import com.unimessage.entity.SysUser;
import com.unimessage.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author 海明
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Resource
    private SysUserMapper userMapper;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<LoginRespDto> login(@RequestBody LoginDto loginDto) {
        LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
        query.eq(SysUser::getUsername, loginDto.getUsername());
        SysUser user = userMapper.selectOne(query);

        if (user == null) {
            return Result.fail("用户不存在");
        }

        // 验证密码（支持 BCrypt 加密和旧的明文密码）
        boolean isMatch = false;
        try {
            if (BCrypt.checkpw(loginDto.getPassword(), user.getPassword())) {
                isMatch = true;
            }
        } catch (Exception e) {
            // 可能是旧的明文密码，继续检查
        }

        if (!isMatch) {
            // 检查是否为旧的明文密码
            if (user.getPassword().equals(loginDto.getPassword())) {
                isMatch = true;
                // 自动升级为加密密码
                user.setPassword(BCrypt.hashpw(loginDto.getPassword()));
                userMapper.updateById(user);
            }
        }

        if (!isMatch) {
            return Result.fail("密码错误");
        }

        if (user.getStatus() != 1) {
            return Result.fail("账号已禁用");
        }

        // Sa-Token 登录
        StpUtil.login(user.getId());

        LoginRespDto resp = new LoginRespDto();
        resp.setToken(StpUtil.getTokenValue());

        SysUserRespDto userResp = new SysUserRespDto();
        BeanUtils.copyProperties(user, userResp);
        resp.setUser(userResp);

        return Result.success(resp);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.success();
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<SysUserRespDto> info() {
        if (!StpUtil.isLogin()) {
            return Result.fail(401, "未登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userMapper.selectById(userId);

        SysUserRespDto userResp = new SysUserRespDto();
        BeanUtils.copyProperties(user, userResp);
        return Result.success(userResp);
    }
}
