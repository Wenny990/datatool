package com.wnhuang.datax.controller;


import com.wnhuang.common.domain.response.ApiResult;
import com.wnhuang.datax.service.DataxPluginService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/dataxPlugin")
public class DataxPluginController {

    @Resource
    DataxPluginService service;

    @GetMapping("/")
    public ApiResult getList() {
        return ApiResult.success(service.list());
    }

}
