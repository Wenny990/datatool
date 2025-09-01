package com.wnhuang.api.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API接口配置实体类
 * @author wnhuang
 * @date 2024/12/01
 */
@Data
@TableName("t_api_config")
public class ApiConfig {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * API接口名称
     */
    private String apiName;

    /**
     * API接口编码，用于对外调用
     */
    private String apiCode;

    /**
     * API接口类型：1-远程HTTP接口，2-数据源SQL查询
     */
    private Integer apiType;

    /**
     * HTTP请求方法：GET、POST等（当apiType=1时使用）
     */
    private String httpMethod;

    /**
     * HTTP请求URL（当apiType=1时使用）
     */
    private String httpUrl;

    /**
     * HTTP请求头（JSON格式，当apiType=1时使用）
     * @deprecated 使用httpRequestConfig替代
     */
    private String httpHeaders;

    /**
     * HTTP请求完整配置（JSON格式，当apiType=1时使用）
     */
    private String httpRequestConfig;

    /**
     * 数据源ID（当apiType=2时使用）
     */
    private Integer repositoryId;

    /**
     * SQL查询语句（当apiType=2时使用）
     */
    private String sqlQuery;

    /**
     * 返回值后处理脚本（支持JavaScript或表达式）
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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新人
     */
    private String updateUser;
}