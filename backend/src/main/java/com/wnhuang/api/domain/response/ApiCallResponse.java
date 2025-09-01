package com.wnhuang.api.domain.response;

import lombok.Data;
import lombok.Builder;

/**
 * API调用响应对象
 * @author wnhuang
 * @date 2024/12/01
 */
@Data
@Builder
public class ApiCallResponse {

    /**
     * 调用是否成功
     */
    private Boolean success;

    /**
     * 响应数据
     */
    private Object data;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行耗时（毫秒）
     */
    private Long executionTime;

    /**
     * API编码
     */
    private String apiCode;

    public static ApiCallResponse success(String apiCode, Object data, Long executionTime) {
        return ApiCallResponse.builder()
                .success(true)
                .apiCode(apiCode)
                .data(data)
                .executionTime(executionTime)
                .build();
    }

    public static ApiCallResponse failure(String apiCode, String errorMessage, Long executionTime) {
        return ApiCallResponse.builder()
                .success(false)
                .apiCode(apiCode)
                .errorMessage(errorMessage)
                .executionTime(executionTime)
                .build();
    }
}