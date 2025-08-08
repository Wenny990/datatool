package com.wnhuang.common.service.monitor;

import com.wnhuang.common.domain.entity.MonitorServerInfo;
import com.wnhuang.common.domain.entity.monitor.Server;
import com.wnhuang.common.service.MonitorServerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitorOsFactory {

    @Autowired
    private MonitorServerInfoService monitorServerInfoService;

    private List<MonitorOsService> monitorOsServiceList;

    @Autowired
    public void setMonitorOsService(List<MonitorOsService> list) {
        this.monitorOsServiceList = list;
    }

    public MonitorOsService createMonitorOsService(MonitorServerInfo serverInfo) {
        for (MonitorOsService t : monitorOsServiceList) {
            if (t.getOsType().equals(serverInfo.getServerOs())) {
                t.setServer(serverInfo);
                return t;
            }
        }
        return null;
    }

    /**
     * 获取服务器的监控信息
     *
     * @param id
     * @return
     */
    public Server getMonitorInfo(Integer id) {
        MonitorServerInfo server = monitorServerInfoService.getById(id);
        MonitorOsService monitorOsService = createMonitorOsService(server);
        return monitorOsService.getServer();
    }

}
