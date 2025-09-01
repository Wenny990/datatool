package com.wnhuang.api.domain.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * API调用请求对象
 * @author wnhuang
 * @date 2024/12/01
 */
@Data
public class ApiCallRequest {

    /**
     * API编码
     */
    @NotBlank(message = "API编码不能为空")
    private String apiCode;

    /**
     * 请求参数
     */
    private Map<String, Object> params;
}