package com.wnhuang.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wnhuang.api.domain.entity.ApiExecuteLog;
import com.wnhuang.api.service.impl.ApiExecuteServiceImpl;
import com.wnhuang.common.domain.response.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * API执行日志查询控制器
 * @author wnhuang
 * @date 2024/12/01
 */
@Slf4j
@RestController
@RequestMapping("/api/log")
public class ApiExecuteLogController {

    @Autowired
    private ApiExecuteServiceImpl apiExecuteService;

    /**
     * 分页查询API执行日志
     */
    @GetMapping("/page")
    public ApiResult<IPage<ApiExecuteLog>> pageExecuteLogs(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String apiCode,
            @RequestParam(required = false) Integer executeStatus,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        Page<ApiExecuteLog> page = new Page<>(current, pageSize);
        
        LambdaQueryWrapper<ApiExecuteLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(apiCode != null, ApiExecuteLog::getApiCode, apiCode)
                   .eq(executeStatus != null, ApiExecuteLog::getExecuteStatus, executeStatus);
        
        // 时间范围查询
        if (startTime != null) {
            queryWrapper.ge(ApiExecuteLog::getExecuteTime, LocalDateTime.parse(startTime));
        }
        if (endTime != null) {
            queryWrapper.le(ApiExecuteLog::getExecuteTime, LocalDateTime.parse(endTime));
        }
        
        queryWrapper.orderByDesc(ApiExecuteLog::getExecuteTime);

        IPage<ApiExecuteLog> result = apiExecuteService.page(page, queryWrapper);
        return ApiResult.success(result);
    }

    /**
     * 查询API执行日志详情
     */
    @GetMapping("/detail/{id}")
    public ApiResult<ApiExecuteLog> getExecuteLogDetail(@PathVariable @NotNull Long id) {
        ApiExecuteLog executeLog = apiExecuteService.getById(id);
        return ApiResult.success(executeLog);
    }

    /**
     * 删除API执行日志
     */
    @DeleteMapping("/delete/{id}")
    public ApiResult<Boolean> deleteExecuteLog(@PathVariable @NotNull Long id) {
        boolean result = apiExecuteService.removeById(id);
        return ApiResult.success(result);
    }

    /**
     * 批量删除API执行日志
     */
    @DeleteMapping("/batch-delete")
    public ApiResult<Boolean> batchDeleteExecuteLogs(@RequestBody java.util.List<Long> ids) {
        boolean result = apiExecuteService.removeByIds(ids);
        return ApiResult.success(result);
    }

    /**
     * 清理过期日志（保留最近30天）
     */
    @DeleteMapping("/clean-expired")
    public ApiResult<Boolean> cleanExpiredLogs() {
        LocalDateTime expiredTime = LocalDateTime.now().minusDays(30);
        
        LambdaQueryWrapper<ApiExecuteLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(ApiExecuteLog::getExecuteTime, expiredTime);
        
        boolean result = apiExecuteService.remove(queryWrapper);
        return ApiResult.success(result);
    }
}