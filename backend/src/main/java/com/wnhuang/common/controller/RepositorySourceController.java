package com.wnhuang.common.controller;


import com.wnhuang.common.domain.entity.RepositorySource;
import com.wnhuang.common.domain.request.SqlParseBaseRequest;
import com.wnhuang.common.domain.response.ApiResult;
import com.wnhuang.common.domain.response.RepositoryBasePageResp;
import com.wnhuang.common.service.RepositorySourceService;
import com.wnhuang.common.service.SqlParseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author wnhuang
 * @Package com.base.controller
 * @date 2022/3/19 21:05
 */
@RestController
@RequestMapping("/repo")
public class RepositorySourceController {

    @Autowired
    private RepositorySourceService service;

    @Autowired
    private SqlParseService sqlParseService;

    @PostMapping
    public ApiResult saveEntity(@RequestBody RepositorySource entity) {
        service.saveOrUpdate(entity);
        return ApiResult.success(entity);
    }

    @PostMapping("/checkRepo")
    public ApiResult validConnection(@RequestBody RepositorySource repositorySource) {
        return ApiResult.success(service.testConnection(repositorySource));
    }

    @GetMapping
    public ApiResult getList() {
        return ApiResult.success(service.list());
    }

    @GetMapping("/{id}")
    public ApiResult getEntityById(@PathVariable("id") Integer id) {
        return ApiResult.success(service.getById(id));
    }

    @DeleteMapping("/{id}")
    public ApiResult removeEntityById(@PathVariable("id") Integer id) {
        return ApiResult.success(service.removeById(id));
    }


    @PostMapping("/execPageQuery")
    public ApiResult execSelectPageSql(@RequestBody RepositoryBasePageResp repoSelectPageRO) {
        return ApiResult.success(service.execPageQuery(repoSelectPageRO));
    }

    @GetMapping("/config")
    public ApiResult getRepoConfig() {
        return ApiResult.success(service.getRepositoryConfigList());
    }


    @PostMapping("/parseSql")
    public ApiResult parseSql(@RequestBody @Valid SqlParseBaseRequest request) {
        return ApiResult.success(sqlParseService.parseSelectSql(request));
    }
}
