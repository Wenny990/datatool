package com.wnhuang.api.domain.entity;

import lombok.Data;

import java.util.List;

/**
 * HTTP请求完整配置
 * @author wnhuang
 * @date 2024/12/01
 */
@Data
public class HttpRequestConfig {

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求URL
     */
    private String url;

    /**
     * 请求头列表
     */
    private List<HttpHeader> headers;

    /**
     * 查询参数列表
     */
    private List<HttpQueryParam> queryParams;

    /**
     * 请求体配置
     */
    private HttpBody body;

    /**
     * 超时时间（毫秒）
     */
    private Integer timeout;

    /**
     * 是否跟随重定向
     */
    private Boolean followRedirects = true;

    /**
     * 是否验证SSL证书
     */
    private Boolean verifySsl = true;

    public HttpRequestConfig() {
        this.timeout = 30000;
        this.followRedirects = true;
        this.verifySsl = true;
    }
}