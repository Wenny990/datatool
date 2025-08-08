package com.wnhuang.common.service.monitor;

import com.wnhuang.common.domain.entity.MonitorServerInfo;
import com.wnhuang.common.domain.entity.monitor.*;

import java.util.List;

public interface MonitorOsService {

    /**
     * 获取服务器信息
     * @return
     */
    default Server getServer() {
        Server server = new Server();
        server.setCpu(getCpu());
        server.setMem(getMem());
        server.setSysFiles(getSysFile());
        server.setSys(getSys());
        return server;
    }

    ;

    /**
     * 操作系统类型
     *
     * @return 操作系统类型
     */
    String getOsType();


    void setServer(MonitorServerInfo server);

    /**
     * 获取cpu使用情况
     *
     * @return
     */
    Cpu getCpu();

    /**
     * 获取缓存信息
     *
     * @return
     */
    Mem getMem();

    /**
     * 获取磁盘信息
     *
     * @return
     */
    List<SysFile> getSysFile();

    /**
     * 获取服务器信息
     *
     * @return 服务器信息
     */
    Sys getSys();
}
