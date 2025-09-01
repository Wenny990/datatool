package com.wnhuang.api.domain.entity;

import lombok.Data;

/**
 * HTTP查询参数配置
 * @author wnhuang
 * @date 2024/12/01
 */
@Data
public class HttpQueryParam {

    /**
     * 参数名称
     */
    private String key;

    /**
     * 参数值
     */
    private String value;

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 描述
     */
    private String description;

    public HttpQueryParam() {}

    public HttpQueryParam(String key, String value) {
        this.key = key;
        this.value = value;
        this.enabled = true;
    }

    public HttpQueryParam(String key, String value, Boolean enabled) {
        this.key = key;
        this.value = value;
        this.enabled = enabled;
    }
}