package com.wnhuang.ogg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wnhuang.ogg.domain.entity.ServerCommand;

/**
* @author Administrator
* @description 针对表【server_command(命令表配置)】的数据库操作Service
* @createDate 2023-04-18 21:22:04
*/
public interface ServerCommandService extends IService<ServerCommand> {

    /**
     * 获取配置的命令
     * @param os 操作系统
     * @param type 指令类型
     * @return 指令
     */
    String getCommand(String os,Integer type);
}
