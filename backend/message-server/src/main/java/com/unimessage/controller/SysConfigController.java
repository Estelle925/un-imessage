package com.unimessage.controller;

import com.unimessage.common.Result;
import com.unimessage.entity.SysConfig;
import com.unimessage.service.SysConfigService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author 海明
 */
@RestController
@RequestMapping("/api/v1/system/config")
public class SysConfigController {

    @Resource
    private SysConfigService sysConfigService;

    @GetMapping
    public Result<SysConfig> getConfig() {
        return Result.success(sysConfigService.getConfig());
    }

    @PostMapping
    public Result<Void> updateConfig(@RequestBody SysConfig config) {
        sysConfigService.updateConfig(config);
        return Result.success();
    }
}
