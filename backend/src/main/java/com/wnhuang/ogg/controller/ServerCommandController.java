package com.wnhuang.ogg.controller;


import com.wnhuang.common.domain.response.ApiResult;
import com.wnhuang.ogg.domain.entity.ServerCommand;
import com.wnhuang.ogg.service.ServerCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/serverCommand")
public class ServerCommandController {

    @Autowired
    ServerCommandService service;

    @GetMapping
    public ApiResult getServerCommandList(){
        return ApiResult.success(service.list());
    }

    @PostMapping
    public ApiResult saveServerCommand(@RequestBody ServerCommand entity){
        return ApiResult.success(service.saveOrUpdate(entity));
    }
}
