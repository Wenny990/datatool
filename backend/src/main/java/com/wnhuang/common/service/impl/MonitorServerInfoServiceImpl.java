package com.wnhuang.common.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wnhuang.common.aviator.AviatorUtils;
import com.wnhuang.common.domain.entity.MonitorServerInfo;
import com.wnhuang.common.mapper.MonitorServerInfoMapper;
import com.wnhuang.common.service.CommandExecuteService;
import com.wnhuang.common.service.MonitorServerInfoService;
import com.wnhuang.common.utils.JschUtil;
import com.wnhuang.ogg.service.ServerCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【monitor_server_info(服务器监控表)】的数据库操作Service实现
 * @createDate 2023-04-17 21:45:39
 */
@Service
@CacheConfig(cacheNames = "server_info")
public class MonitorServerInfoServiceImpl extends ServiceImpl<MonitorServerInfoMapper, MonitorServerInfo>
        implements MonitorServerInfoService {

    @Autowired
    ServerCommandService serverCommandService;

    @Autowired
    CommandExecuteService commandExecuteService;


    @Override
    @Cacheable(key = "#id")
    public MonitorServerInfo getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdate(MonitorServerInfo entity) {
        JschUtil.closeSession(entity.getServerIp());
        return super.saveOrUpdate(entity);
    }

    @Override
    @Cacheable(key = "'all'")
    public List<MonitorServerInfo> list(Wrapper<MonitorServerInfo> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public String verifyServer(MonitorServerInfo serverInfo) {
        String command = serverCommandService.getCommand(serverInfo.getServerOs(), 9);
        command = AviatorUtils.compileSql(command, JSONUtil.parseObj(serverInfo));
        String result = commandExecuteService.executeCommand(serverInfo, command);
        return result;
    }



    @Override
    @CacheEvict(allEntries = true)
    public Boolean updateIsUploaded(MonitorServerInfo serverInfo) {
        return this.lambdaUpdate().set(MonitorServerInfo::getIsUploaded, serverInfo.getIsUploaded())
                .eq(MonitorServerInfo::getId, serverInfo.getId()).update();
    }
}




