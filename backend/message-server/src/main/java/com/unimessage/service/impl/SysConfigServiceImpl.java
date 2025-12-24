package com.unimessage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unimessage.entity.SysConfig;
import com.unimessage.mapper.SysConfigMapper;
import com.unimessage.service.SysConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 海明
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public SysConfig getConfig() {
        return this.getOne(new LambdaQueryWrapper<SysConfig>().last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(SysConfig config) {
        SysConfig exist = getConfig();
        if (exist != null) {
            config.setId(exist.getId());
            this.updateById(config);
        } else {
            this.save(config);
        }
    }
}
