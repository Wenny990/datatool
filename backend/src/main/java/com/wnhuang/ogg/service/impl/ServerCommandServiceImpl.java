package com.wnhuang.ogg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.wnhuang.common.mapper.ServerCommandMapper;
import com.wnhuang.common.utils.DesUtil;
import com.wnhuang.ogg.domain.entity.ServerCommand;
import com.wnhuang.ogg.service.ServerCommandService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
* @author Administrator
* @description 针对表【server_command(命令表配置)】的数据库操作Service实现
* @createDate 2023-04-18 21:22:04
*/
@Service
@CacheConfig(cacheNames = "server_command")
public class ServerCommandServiceImpl extends ServiceImpl<ServerCommandMapper, ServerCommand>
    implements ServerCommandService {

    /**
     * 获取配置的命令
     * @param os 操作系统
     * @param type 指令类型
     * @return 指令
     */
    @Cacheable(key = "#os + '-' + #type")
    @Override
    public String getCommand(String os,Integer type){
        ServerCommand command = this.lambdaQuery().eq(ServerCommand::getServerOs,os)
                .eq(ServerCommand::getType,type)
                .one();
        Assert.notNull(command,"未配置该命令，os：" + os + " ，" + " type：" + type);
        return command.getCommand();
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdate(ServerCommand entity) {
        entity.setCommand(DesUtil.decrypt(entity.getCommand()));
        return super.saveOrUpdate(entity);
    }
}




