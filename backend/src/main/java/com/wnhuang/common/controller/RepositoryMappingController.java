package com.wnhuang.common.controller;


import com.wnhuang.common.domain.entity.RepositoryMapping;
import com.wnhuang.common.domain.response.ApiResult;
import com.wnhuang.common.service.RepositoryColumnTypeService;
import com.wnhuang.common.service.RepositoryMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wnhuang
 * @date 2024/3/19 21:05
 */
@RestController
@RequestMapping("/repo_mapping")
public class RepositoryMappingController {

    @Autowired
    private RepositoryMappingService service;

    @Autowired
    private RepositoryColumnTypeService repositoryColumnTypeService;

    @PostMapping
    public ApiResult saveEntity(@RequestBody RepositoryMapping entity) {
        service.saveOrUpdate(entity);
        return ApiResult.success(entity);
    }

    @GetMapping
    public ApiResult getList() {
        return ApiResult.success(service.list());
    }


    @DeleteMapping("/{id}")
    public ApiResult removeEntityById(@PathVariable("id") Integer id) {
        return ApiResult.success(service.removeById(id));
    }

    @GetMapping("columnType")
    public ApiResult getColumnList() {
        return ApiResult.success(repositoryColumnTypeService.list());
    }

}
