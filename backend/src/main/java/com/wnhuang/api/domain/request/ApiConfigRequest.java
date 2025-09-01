package com.wnhuang.api.domain.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * API配置请求对象
 * @author wnhuang
 * @date 2024/12/01
 */
@Data
public class ApiConfigRequest {

    /**
     * 主键ID（更新时使用）
     */
    private Integer id;

    /**
     * API接口名称
     */
    @NotBlank(message = "API接口名称不能为空")
    private String apiName;

    /**
     * API接口编码
     */
    @NotBlank(message = "API接口编码不能为空")
    private String apiCode;

    /**
     * API接口类型：1-远程HTTP接口，2-数据源SQL查询
     */
    @NotNull(message = "API接口类型不能为空")
    private Integer apiType;

    /**
     * HTTP请求方法
     */
    private String httpMethod;

    /**
     * HTTP请求URL
     */
    private String httpUrl;

    /**
     * HTTP请求头（JSON格式）
     * @deprecated 使用httpRequestConfig替代
     */
    private String httpHeaders;

    /**
     * HTTP请求完整配置（JSON格式）
     */
    private String httpRequestConfig;

    /**
     * 数据源ID
     */
    private Integer repositoryId;

    /**
     * SQL查询语句
     */
    private String sqlQuery;

    /**
     * 返回值后处理脚本
     */
    private String postProcessScript;

    /**
     * 超时时间（毫秒）
     */
    private Integer timeout;

    /**
     * 是否启用：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;
}