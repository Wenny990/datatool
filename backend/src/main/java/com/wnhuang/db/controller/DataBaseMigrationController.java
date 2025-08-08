package com.wnhuang.db.controller;

import com.wnhuang.common.domain.response.ApiResult;
import com.wnhuang.db.domain.entity.TableColumnInfo;
import com.wnhuang.db.domain.request.MigrationTableColumnRequest;
import com.wnhuang.db.service.DataBaseMigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wnhuang
 * @date 2025/6/17 23:09
 */
@RestController
@RequestMapping("dbMigration")
public class DataBaseMigrationController {

    @Autowired
    private DataBaseMigrationService dataBaseMigrationService;

    @PostMapping("conversionColumn")
    public ApiResult<List<TableColumnInfo>> getTargetTableColumnInfo(@RequestBody MigrationTableColumnRequest request) {
        return ApiResult.success(dataBaseMigrationService.getConversionColumn(request.getColumnInfoList(), request.getConversionConfig()));
    }
}
