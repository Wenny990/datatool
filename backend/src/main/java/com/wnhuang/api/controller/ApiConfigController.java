package com.wnhuang.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wnhuang.api.domain.entity.ApiConfig;
import com.wnhuang.api.domain.request.ApiConfigRequest;
import com.wnhuang.api.service.ApiConfigService;
import com.wnhuang.common.domain.response.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * API配置管理控制器
 * @author wnhuang
 * @date 2024/12/01
 */
@Slf4j
@RestController
@RequestMapping("/apiConfig")
public class ApiConfigController {

    @Autowired
    private ApiConfigService apiConfigService;

    /**
     * 创建API配置
     */
    @PostMapping("/create")
    public ApiResult<Boolean> createApiConfig(@RequestBody @Valid ApiConfigRequest request) {
        boolean result = apiConfigService.createApiConfig(request);
        return ApiResult.success(result);
    }

    /**
     * 更新API配置
     */
    @PostMapping("/update")
    public ApiResult<Boolean> updateApiConfig(@RequestBody @Valid ApiConfigRequest request) {
        boolean result = apiConfigService.updateApiConfig(request);
        return ApiResult.success(result);
    }

    /**
     * 删除API配置
     */
    @DeleteMapping("/delete/{id}")
    public ApiResult<Boolean> deleteApiConfig(@PathVariable @NotNull Integer id) {
        boolean result = apiConfigService.deleteApiConfig(id);
        return ApiResult.success(result);
    }

    /**
     * 分页查询API配置
     */
    @GetMapping("/page")
    public ApiResult<IPage<ApiConfig>> pageApiConfigs(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String apiName,
            @RequestParam(required = false) Integer apiType,
            @RequestParam(required = false) Integer status) {
        
        IPage<ApiConfig> result = apiConfigService.pageApiConfigs(current, pageSize, apiName, apiType, status);
        return ApiResult.success(result);
    }

    /**
     * 根据ID查询API配置详情
     */
    @GetMapping("/detail/{id}")
    public ApiResult<ApiConfig> getApiConfigDetail(@PathVariable @NotNull Integer id) {
        ApiConfig apiConfig = apiConfigService.getById(id);
        return ApiResult.success(apiConfig);
    }

    /**
     * 根据API编码查询配置
     */
    @GetMapping("/code/{apiCode}")
    public ApiResult<ApiConfig> getApiConfigByCode(@PathVariable @NotNull String apiCode) {
        ApiConfig apiConfig = apiConfigService.getByApiCode(apiCode);
        return ApiResult.success(apiConfig);
    }

    /**
     * 检查API编码是否存在
     */
    @GetMapping("/check-code")
    public ApiResult<Boolean> checkApiCodeExists(
            @RequestParam String apiCode,
            @RequestParam(required = false) Integer excludeId) {
        
        boolean exists = apiConfigService.checkApiCodeExists(apiCode, excludeId);
        return ApiResult.success(exists);
    }

    /**
     * 启用/禁用API配置
     */
    @PostMapping("/status/{id}/{status}")
    public ApiResult<Boolean> updateStatus(
            @PathVariable @NotNull Integer id,
            @PathVariable @NotNull Integer status) {
        
        boolean result = apiConfigService.updateStatus(id, status);
        return ApiResult.success(result);
    }

    /**
     * 获取所有启用的API配置列表
     */
    @GetMapping("/list/enabled")
    public ApiResult<java.util.List<ApiConfig>> getEnabledApiConfigs() {
        java.util.List<ApiConfig> apiConfigs = apiConfigService.lambdaQuery()
                .eq(ApiConfig::getStatus, 1)
                .orderByDesc(ApiConfig::getUpdateTime)
                .list();
        return ApiResult.success(apiConfigs);
    }
}