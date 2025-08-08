package com.wnhuang.common.controller;


import com.wnhuang.common.domain.entity.MonitorServerInfo;
import com.wnhuang.common.domain.response.ApiResult;
import com.wnhuang.common.service.MonitorServerInfoService;
import com.wnhuang.common.service.monitor.MonitorOsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wnhuang
 * @created by Administrator on 2023-04-17-23:14
 */
@RestController
@RequestMapping("/server")
public class MonitorServerInfoController {

    @Autowired
    MonitorServerInfoService monitorServerInfoService;

    @Autowired
    MonitorOsFactory monitorOsFactory;

    @GetMapping
    public ApiResult getServerList(){
        return ApiResult.success(monitorServerInfoService.list());
    }

    @GetMapping("/{id}")
    public ApiResult getServer(@PathVariable("id") Integer id){
        return ApiResult.success(monitorServerInfoService.getById(id));
    }

    @PostMapping
    public ApiResult saveServer(@RequestBody MonitorServerInfo entity){
        return ApiResult.success(monitorServerInfoService.saveOrUpdate(entity));
    }

    @DeleteMapping("/{id}")
    public ApiResult removeServer(@PathVariable("id") Integer id){
        return ApiResult.success(monitorServerInfoService.removeById(id));
    }

    @PostMapping("/test")
    public ApiResult verifyServer(@RequestBody MonitorServerInfo entity){
        return ApiResult.success(monitorServerInfoService.verifyServer(entity));
    }

    @GetMapping("/monitor/{id}")
    public ApiResult monitorServerInfo(@PathVariable("id") Integer id){
        return ApiResult.success(monitorOsFactory.getMonitorInfo(id));
    }
}
