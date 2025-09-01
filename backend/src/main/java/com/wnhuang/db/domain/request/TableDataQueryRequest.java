package com.wnhuang.db.domain.request;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;

/**
 * 表数据查询请求
 * @author wnhuang
 * @date 2024/12/01
 */
@Data
public class TableDataQueryRequest {
    
    /**
     * 数据源ID
     */
    @NotNull(message = "数据源ID不能为空")
    private Integer repositoryId;
    
    /**
     * Schema名称
     */
    private String schemaName;
    
    /**
     * 表名
     */
    @NotNull(message = "表名不能为空")
    private String tableName;
    
    /**
     * 页码
     */
    @Min(value = 1, message = "页码最小为1")
    private Integer current = 1;
    
    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小最小为1")
    private Integer pageSize = 20;
    
    /**
     * 排序字段
     */
    private String orderBy;
    
    /**
     * 排序方向 (ASC/DESC)
     */
    private String orderDirection = "ASC";
    
    /**
     * 查询条件
     */
    private String whereClause;
}