package com.wnhuang.ogg.controller;


import com.wnhuang.common.domain.response.ApiResult;
import com.wnhuang.ogg.domain.entity.OggConfig;
import com.wnhuang.ogg.domain.entity.OggGenerateConfig;
import com.wnhuang.ogg.domain.entity.OggProcess;
import com.wnhuang.ogg.domain.request.OggCommandRequest;
import com.wnhuang.ogg.service.OggProcessService;
import kotlin.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author wnhuang
 * @created by Administrator on 2023-04-18-23:05
 */
@RestController
@RequestMapping("/process")
public class OggProcessController {

    @Autowired
    OggProcessService oggProcessService;

    @GetMapping("/{serverId}")
    public ApiResult getProcessList(@PathVariable("serverId") Integer serverId){
        return ApiResult.success(oggProcessService.getOggProcess(serverId));
    }

    @GetMapping("/{serverId}/{processName}")
    public ApiResult<OggProcess> getProcessDetail(@PathVariable("serverId") Integer serverId,@PathVariable("processName") String processName){
        return ApiResult.success(oggProcessService.getProcessDetail(serverId,processName));
    }

    @GetMapping("/params/{serverId}/{processName}")
    public ApiResult<OggProcess> getProcessParams(@PathVariable("serverId") Integer serverId,@PathVariable("processName") String processName){
        return ApiResult.success(oggProcessService.getProcessParams(serverId,processName));
    }

    @GetMapping("/report/{serverId}/{processName}")
    public ApiResult<OggProcess> getProcessReport(@PathVariable("serverId") Integer serverId,@PathVariable("processName") String processName){
        return ApiResult.success(oggProcessService.getProcessReport(serverId,processName));
    }

    @GetMapping("/info/{serverId}/{processName}")
    public ApiResult<OggProcess> getProcessInfo(@PathVariable("serverId") Integer serverId,@PathVariable("processName") String processName){
        return ApiResult.success(oggProcessService.getProcessInfo(serverId,processName));
    }

    @GetMapping("/{serverId}/{processName}/{isStart}")
    public ApiResult openOrCloseProcess(@PathVariable("serverId") Integer serverId,@PathVariable("processName") String processName,@PathVariable("isStart") Boolean isStart){
        return ApiResult.success(oggProcessService.startOrStopProcess(serverId,processName,isStart));
    }

    @GetMapping("/delete/{serverId}/{processName}")
    public ApiResult deleteProcess(@PathVariable("serverId") Integer serverId,@PathVariable("processName") String processName){
        return ApiResult.success(oggProcessService.deleteProcess(serverId,processName));
    }

    @PostMapping("/params")
    public ApiResult saveProcessParams(@RequestBody OggProcess oggProcess){
        return ApiResult.success(oggProcessService.editProcessParams(oggProcess));
    }

    @PostMapping("/generate")
    public ApiResult<OggConfig> generateOggConfig(@RequestBody OggGenerateConfig oggGenerateConfig){
        return ApiResult.success(oggProcessService.generateOggConfig(oggGenerateConfig));
    }

    @PostMapping("/exec")
    public ApiResult executeMultipleCommands(@RequestBody OggConfig oggConfig){
        return ApiResult.success(oggProcessService.executeMultipleCommands(oggConfig));
    }

    @GetMapping("/monitor")
    public ApiResult getAllProcessInfo(){
        return ApiResult.success(oggProcessService.getOggMonitorInfo());
    }

    @GetMapping("/monitor/nocache")
    public ApiResult getAllProcessInfoNoCache(){
        return ApiResult.success(oggProcessService.getOggMonitorInfo());
    }

    @PostMapping("/commandPreview")
    public ApiResult<String> commandPreview(@Valid @RequestBody OggCommandRequest request){
        return ApiResult.success(oggProcessService.commandPreview(request));
    }
}
