package com.wnhuang.datax.controller;


import com.wnhuang.common.domain.response.ApiResult;
import com.wnhuang.datax.domain.entity.DataxJobGroup;
import com.wnhuang.datax.service.DataxJobGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dataxJobGroup")
public class DataxJobGroupController {

    @Autowired
    DataxJobGroupService service;

    @GetMapping()
    public ApiResult getList() {
        return ApiResult.success(service.list());
    }

    @PostMapping
    public ApiResult<DataxJobGroup> saveDataxJobGroup(@RequestBody DataxJobGroup entity) {
        service.saveOrUpdate(entity);
        return ApiResult.success(entity);
    }

    @DeleteMapping("/{id}")
    public ApiResult deleteDataxJobGroup(@PathVariable Integer id) {
        return ApiResult.success(service.removeById(id));
    }
}
