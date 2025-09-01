package com.wnhuang.api.service;

import com.wnhuang.api.domain.request.ApiCallRequest;
import com.wnhuang.api.domain.response.ApiCallResponse;

/**
 * API执行服务接口
 * @author wnhuang
 * @date 2024/12/01
 */
public interface ApiExecuteService {

    /**
     * 执行API调用
     *
     * @param request API调用请求
     * @return API调用响应
     */
    ApiCallResponse executeApi(ApiCallRequest request);

    /**
     * 执行API调用（带IP记录）
     *
     * @param request   API调用请求
     * @param requestIp 请求IP
     * @return API调用响应
     */
    ApiCallResponse executeApi(ApiCallRequest request, String requestIp);
}