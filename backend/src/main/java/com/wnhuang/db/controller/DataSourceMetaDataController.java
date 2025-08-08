package com.wnhuang.db.controller;

import com.wnhuang.db.domain.request.DbSchemaMetaRequest;
import com.wnhuang.db.domain.request.DbTableMetaRequest;
import com.wnhuang.common.domain.response.ApiResult;
import com.wnhuang.db.service.DataBaseMetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author wnhuang
 * @date 2024/11/17 16:41
 */
@RestController
@RequestMapping("dbMeta")
public class DataSourceMetaDataController {

    @Autowired
    DataBaseMetaDataService service;

    @GetMapping("/schema")
    public ApiResult getSchemaList(@RequestParam @NotNull(message = "数据源有误！") Integer repositoryId){
        return ApiResult.success(service.getSchemaList(repositoryId));
    }

    @GetMapping("/table")
    public ApiResult getTableList(@Valid DbSchemaMetaRequest dbSchemaMetaRequest){
        return ApiResult.success(service.getTableList(dbSchemaMetaRequest));
    }

    @GetMapping("/column")
    public ApiResult getTableColumnList(@Valid DbTableMetaRequest dbTableMetaRequest){
        return ApiResult.success(service.getTableColumnList(dbTableMetaRequest));
    }

    @GetMapping("/index")
    public ApiResult getTableIndexList(@Valid DbTableMetaRequest dbTableMetaRequest){
        return ApiResult.success(service.getTableIndexList(dbTableMetaRequest));
    }
}
