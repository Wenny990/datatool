package com.wnhuang.db.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wnhuang.common.domain.response.ApiResult;
import com.wnhuang.db.domain.entity.DatabaseSummary;
import com.wnhuang.db.domain.request.TableDataQueryRequest;
import com.wnhuang.db.service.DatabaseMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 数据库监控控制器
 * @author wnhuang
 * @date 2024/12/01
 */
@RestController
@RequestMapping("/dbMonitor")
public class DatabaseMonitorController {
    
    @Autowired
    private DatabaseMonitorService databaseMonitorService;
    
    /**
     * 获取数据库概要信息
     */
    @GetMapping("/summary/{repositoryId}")
    public ApiResult<DatabaseSummary> getDatabaseSummary(
            @PathVariable @NotNull(message = "数据源ID不能为空") Integer repositoryId) {
        DatabaseSummary summary = databaseMonitorService.getDatabaseSummary(repositoryId);
        return ApiResult.success(summary);
    }
    
    /**
     * 获取所有数据源的概要信息
     */
    @GetMapping("/summary/all")
    public ApiResult<List<DatabaseSummary>> getAllDatabaseSummaries() {
        List<DatabaseSummary> summaries = databaseMonitorService.getAllDatabaseSummaries();
        return ApiResult.success(summaries);
    }
    
    /**
     * 测试数据库连接
     */
    @GetMapping("/test-connection/{repositoryId}")
    public ApiResult<Boolean> testConnection(
            @PathVariable @NotNull(message = "数据源ID不能为空") Integer repositoryId) {
        Boolean result = databaseMonitorService.testConnection(repositoryId);
        return ApiResult.success(result);
    }
    
    /**
     * 查询表数据（分页）
     */
    @PostMapping("/table/data")
    public ApiResult<IPage<LinkedHashMap<String, Object>>> getTableData(
            @RequestBody @Valid TableDataQueryRequest request) {
        IPage<LinkedHashMap<String, Object>> result = databaseMonitorService.getTableData(request);
        return ApiResult.success(result);
    }
    
    /**
     * 获取表的记录总数
     */
    @GetMapping("/table/count")
    public ApiResult<Long> getTableRowCount(
            @RequestParam @NotNull(message = "数据源ID不能为空") Integer repositoryId,
            @RequestParam String schemaName,
            @RequestParam @NotNull(message = "表名不能为空") String tableName) {
        Long count = databaseMonitorService.getTableRowCount(repositoryId, schemaName, tableName);
        return ApiResult.success(count);
    }
    
    /**
     * 获取数据库运行状态信息
     */
    @GetMapping("/status/{repositoryId}")
    public ApiResult<LinkedHashMap<String, Object>> getDatabaseStatus(
            @PathVariable @NotNull(message = "数据源ID不能为空") Integer repositoryId) {
        LinkedHashMap<String, Object> status = databaseMonitorService.getDatabaseStatus(repositoryId);
        return ApiResult.success(status);
    }
}