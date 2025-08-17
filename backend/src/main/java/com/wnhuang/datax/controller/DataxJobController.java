package com.wnhuang.datax.controller;


import com.wnhuang.common.domain.response.ApiResult;
import com.wnhuang.datax.domain.entity.DataxJob;
import com.wnhuang.datax.service.DataxJobService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dataxJob")
public class DataxJobController {

    @Resource
    DataxJobService service;

    @GetMapping("/group/{groupId}")
    public ApiResult getList(@PathVariable Integer groupId) {
        return ApiResult.success(service.listByGroupId(groupId));
    }

    @GetMapping("/{id}")
    public ApiResult<DataxJob> getDataxJob(@PathVariable Integer id) {
        return ApiResult.success(service.getById(id));
    }

    @GetMapping("/upload/{id}")
    public ApiResult<DataxJob> uploadJob(@PathVariable Integer id) {
        return ApiResult.success(service.uploadJson(id));
    }

    @PostMapping
    public ApiResult<DataxJob> saveDataxJob(@RequestBody DataxJob entity) {
        service.saveOrUpdate(entity);
        return ApiResult.success(entity);
    }

    @PostMapping("/execute/{id}")
    public ApiResult executeJob(@PathVariable Integer id) {
        return ApiResult.success(service.executeDataxJob(id));
    }

    @PostMapping("/executeByRange/{id}")
    public ApiResult executeByRange(@PathVariable Integer id) {
        return ApiResult.success(service.executeDataxJobByRange(id));
    }

    @PostMapping("/executeByRangeSync/{id}")
    public ApiResult executeByRangeSync(@PathVariable Integer id) {
        return ApiResult.success(service.executeDataxJobByRangeAsync(id));
    }

    @GetMapping("/execute/{uuid}")
    public ApiResult getExecuteLog(@PathVariable String uuid) {
        return ApiResult.success(service.getJobRunLog(uuid));
    }

    @GetMapping("/execute/detail/{uuid}")
    public ApiResult getJobRunLogDetail(@PathVariable String uuid) {
        return ApiResult.success(service.getJobRunLogContent(uuid));
    }

    @GetMapping("/execute")
    public ApiResult getExecuteLog() {
        return ApiResult.success(service.getAllDataxJobRunLog());
    }

    @PostMapping("/execute")
    public ApiResult deleteExecuteLog(@RequestBody Map<String, Object> params) {
        List<String> keys = (List<String>) params.get("keys");
        return ApiResult.success(service.removeExecKeys(keys));
    }

    @DeleteMapping("/execute/{uuid}")
    public ApiResult<Boolean> stopExecuteLog(@PathVariable String uuid) {
        return ApiResult.success(service.killDataxJob(uuid));
    }

    @DeleteMapping("/{id}")
    public ApiResult deleteDataxJob(@PathVariable Integer id) {
        return ApiResult.success(service.removeById(id));
    }


}
