package com.wnhuang.api.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wnhuang.api.domain.entity.ApiConfig;
import com.wnhuang.api.domain.entity.ApiExecuteLog;
import com.wnhuang.api.domain.entity.HttpRequestConfig;
import com.wnhuang.api.domain.request.ApiCallRequest;
import com.wnhuang.api.domain.response.ApiCallResponse;
import com.wnhuang.api.mapper.ApiExecuteLogMapper;
import com.wnhuang.api.service.ApiConfigService;
import com.wnhuang.api.service.ApiExecuteService;
import com.wnhuang.api.utils.HttpClientUtil;
import com.wnhuang.api.utils.ParamProcessUtil;
import com.wnhuang.common.exception.BusinessException;
import com.wnhuang.common.service.RepositorySourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * API执行服务实现类
 * @author wnhuang
 * @date 2024/12/01
 */
@Slf4j
@Service
public class ApiExecuteServiceImpl extends ServiceImpl<ApiExecuteLogMapper, ApiExecuteLog> implements ApiExecuteService {

    @Autowired
    private ApiConfigService apiConfigService;

    @Autowired
    private HttpClientUtil httpClientUtil;

    @Autowired
    private ParamProcessUtil paramProcessUtil;

    @Autowired
    private RepositorySourceService repositorySourceService;

    @Override
    public ApiCallResponse executeApi(ApiCallRequest request) {
        return executeApi(request, null);
    }

    @Override
    public ApiCallResponse executeApi(ApiCallRequest request, String requestIp) {
        long startTime = System.currentTimeMillis();
        String apiCode = request.getApiCode();
        
        try {
            log.info("开始执行API调用，apiCode: {}, params: {}", apiCode, request.getParams());

            // 1. 查询API配置
            ApiConfig apiConfig = apiConfigService.getByApiCode(apiCode);
            if (apiConfig == null) {
                throw new RuntimeException("API配置不存在或已禁用");
            }

            if (apiConfig.getStatus() != 1) {
                throw new RuntimeException("API已禁用");
            }

            // 2. 直接使用用户传入的参数
            Map<String, Object> processedParams = request.getParams() != null ? request.getParams() : new HashMap<>();

            // 3. 执行API调用
            Object result;
            if (apiConfig.getApiType() == 1) {
                // HTTP接口调用
                result = executeHttpApi(apiConfig, processedParams);
            } else if (apiConfig.getApiType() == 2) {
                // 数据源SQL查询
                result = executeSqlApi(apiConfig, processedParams);
            } else {
                throw new RuntimeException("不支持的API类型");
            }

            // 4. 执行后处理脚本
            if (StrUtil.isNotBlank(apiConfig.getPostProcessScript())) {
                result = paramProcessUtil.executePostProcessScript(apiConfig.getPostProcessScript(), result);
            }

            long executionTime = System.currentTimeMillis() - startTime;

            // 5. 记录执行日志
//            saveExecuteLog(apiConfig, request.getParams(), result, true, null, executionTime, requestIp);

            log.info("API调用执行成功，apiCode: {}, 耗时: {}ms", apiCode, executionTime);
            return ApiCallResponse.success(apiCode, result, executionTime);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("API调用执行失败，apiCode: {}, 错误信息: {}", apiCode, e.getMessage(), e);

            // 记录错误日志
            try {
                ApiConfig apiConfig = apiConfigService.getByApiCode(apiCode);
                if (apiConfig != null) {
                    saveExecuteLog(apiConfig, request.getParams(), null, false, e.getMessage(), executionTime, requestIp);
                }
            } catch (Exception logError) {
                log.error("记录API执行错误日志失败", logError);
            }

            return ApiCallResponse.failure(apiCode, e.getMessage(), executionTime);
        }
    }

    /**
     * 执行HTTP接口调用
     */
    private Object executeHttpApi(ApiConfig apiConfig, Map<String, Object> params) {
        try {
            // 优先使用新的HTTP配置结构
            if (StrUtil.isNotBlank(apiConfig.getHttpRequestConfig())) {
                return executeHttpApiWithConfig(apiConfig, params);
            } else {
                // 兼容旧的HTTP配置格式
                return executeHttpApiLegacy(apiConfig, params);
            }
        } catch (Exception e) {
            log.error("HTTP接口调用失败，URL: {}, 方法: {}", apiConfig.getHttpUrl(), apiConfig.getHttpMethod(), e);
            throw new RuntimeException("HTTP接口调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用新的HTTP配置结构执行请求
     */
    private Object executeHttpApiWithConfig(ApiConfig apiConfig, Map<String, Object> params) {
        try {
            // 解析HTTP请求配置
            HttpRequestConfig httpConfig = JSONUtil.toBean(apiConfig.getHttpRequestConfig(), HttpRequestConfig.class);
            
            // 执行HTTP请求
            String response = httpClientUtil.executeHttpRequest(httpConfig, params);
            
            // 尝试解析JSON响应
            try {
                return JSONUtil.parse(response);
            } catch (Exception e) {
                // 如果不是JSON格式，直接返回字符串
                return response;
            }
        } catch (Exception e) {
            log.error("使用新HTTP配置执行请求失败", e);
            throw new RuntimeException("使用新HTTP配置执行请求失败: " + e.getMessage(), e);
        }
    }

    /**
     * 兼容旧的HTTP配置格式
     */
    private Object executeHttpApiLegacy(ApiConfig apiConfig, Map<String, Object> params) {
        try {
            // 解析HTTP请求头
            Map<String, String> headers = new HashMap<>();
            if (StrUtil.isNotBlank(apiConfig.getHttpHeaders())) {
                headers = JSONUtil.toBean(apiConfig.getHttpHeaders(), Map.class);
            }

            // 设置默认超时时间
            int timeout = apiConfig.getTimeout() != null ? apiConfig.getTimeout() : 30000;

            String response;
            String httpMethod = apiConfig.getHttpMethod().toUpperCase();

            switch (httpMethod) {
                case "GET":
                    response = httpClientUtil.doGet(apiConfig.getHttpUrl(), headers, params, timeout);
                    break;
                case "POST":
                    response = httpClientUtil.doPost(apiConfig.getHttpUrl(), headers, params, timeout);
                    break;
                case "PUT":
                    response = httpClientUtil.doPut(apiConfig.getHttpUrl(), headers, params, timeout);
                    break;
                case "DELETE":
                    response = httpClientUtil.doDelete(apiConfig.getHttpUrl(), headers, params, timeout);
                    break;
                default:
                    throw new RuntimeException("不支持的HTTP方法: " + httpMethod);
            }

            // 尝试解析JSON响应
            try {
                return JSONUtil.parse(response);
            } catch (Exception e) {
                // 如果不是JSON格式，直接返回字符串
                return response;
            }

        } catch (Exception e) {
            log.error("使用旧HTTP配置执行请求失败", e);
            throw new RuntimeException("使用旧HTTP配置执行请求失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行数据源SQL查询
     */
    private Object executeSqlApi(ApiConfig apiConfig, Map<String, Object> params) {
        try {
            // 处理SQL参数
            String processedSql = paramProcessUtil.processSqlParams(apiConfig.getSqlQuery(), params);
            
            log.info("执行SQL查询，数据源ID: {}, SQL: {}", apiConfig.getRepositoryId(), processedSql);

            // 执行SQL查询
            List<LinkedHashMap<String, Object>> queryResult = repositorySourceService.execQuery(
                    String.valueOf(apiConfig.getRepositoryId()), processedSql);

            return queryResult;

        } catch (Exception e) {
            log.error("SQL查询执行失败，数据源ID: {}, SQL: {}", apiConfig.getRepositoryId(), apiConfig.getSqlQuery(), e);
            throw new BusinessException("SQL查询执行失败: " + e.getMessage());
        }
    }

    /**
     * 保存执行日志
     */
    private void saveExecuteLog(ApiConfig apiConfig, Map<String, Object> requestParams, Object responseResult,
                               boolean success, String errorMessage, long executionTime, String requestIp) {
        try {
            ApiExecuteLog executeLog = new ApiExecuteLog();
            executeLog.setApiConfigId(apiConfig.getId());
            executeLog.setApiCode(apiConfig.getApiCode());
            executeLog.setRequestParams(JSONUtil.toJsonStr(requestParams));
            executeLog.setResponseResult(success ? JSONUtil.toJsonStr(responseResult) : null);
            executeLog.setExecuteStatus(success ? 1 : 0);
            executeLog.setErrorMessage(errorMessage);
            executeLog.setExecutionTime(executionTime);
            executeLog.setExecuteTime(LocalDateTime.now());
            executeLog.setRequestIp(requestIp);

            save(executeLog);
        } catch (Exception e) {
            log.error("保存API执行日志失败", e);
        }
    }
}