package com.wnhuang.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wnhuang.common.domain.entity.MonitorServerInfo;
import com.wnhuang.common.domain.entity.monitor.Server;


/**
* @author Administrator
* @description 针对表【monitor_server_info(服务器监控表)】的数据库操作Service
* @createDate 2023-04-17 21:45:39
*/
public interface MonitorServerInfoService extends IService<MonitorServerInfo> {

    /**
     * 验证服务器能否连上OGG
     * @return
     */
    String verifyServer(MonitorServerInfo serverInfo);



    Boolean updateIsUploaded(MonitorServerInfo serverInfo);
}
