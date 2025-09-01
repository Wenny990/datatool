package com.wnhuang.api.domain.entity;

import lombok.Data;

/**
 * HTTP请求头配置
 * @author wnhuang
 * @date 2024/12/01
 */
@Data
public class HttpHeader {

    /**
     * 请求头名称
     */
    private String key;

    /**
     * 请求头值
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

    public HttpHeader() {}

    public HttpHeader(String key, String value) {
        this.key = key;
        this.value = value;
        this.enabled = true;
    }

    public HttpHeader(String key, String value, Boolean enabled) {
        this.key = key;
        this.value = value;
        this.enabled = enabled;
    }
}