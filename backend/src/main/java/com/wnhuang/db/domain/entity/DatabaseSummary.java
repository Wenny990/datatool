package com.wnhuang.db.domain.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 数据库概要信息实体类
 * @author wnhuang
 * @date 2024/12/01
 */
@Data
public class DatabaseSummary {
    
    /**
     * 数据源ID
     */
    private Integer repositoryId;
    
    /**
     * 数据源名称
     */
    private String repositoryName;
    
    /**
     * 数据库类型
     */
    private String databaseType;
    
    /**
     * 数据库版本
     */
    private String databaseVersion;
    
    /**
     * 数据库URL
     */
    private String databaseUrl;
    
    /**
     * 连接状态 (1:正常 0:异常)
     */
    private Integer connectionStatus;

    /**
     * Catalog总数
     */
    private Integer catalogCount;
    
    /**
     * Schema总数
     */
    private Integer schemaCount;
    
    /**
     * 表总数
     */
    private Integer tableCount;
    
    /**
     * 视图总数
     */
    private Integer viewCount;
    
    /**
     * 数据库大小
     */
    private String databaseSize;
    
    /**
     * 最大连接数
     */
    private Integer maxConnections;
    
    /**
     * 当前连接数
     */
    private Integer currentConnections;
    
    /**
     * 运行时间(秒)
     */
    private Long uptime;
    
    /**
     * 最后检查时间
     */
    private LocalDateTime lastCheckTime;
    
    /**
     * 错误信息
     */
    private String errorMessage;
}