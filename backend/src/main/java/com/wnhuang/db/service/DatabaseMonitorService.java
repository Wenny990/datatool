package com.wnhuang.db.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wnhuang.db.domain.entity.DatabaseSummary;
import com.wnhuang.db.domain.request.TableDataQueryRequest;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 数据库监控服务接口
 * @author wnhuang
 * @date 2024/12/01
 */
public interface DatabaseMonitorService {
    
    /**
     * 获取数据库概要信息
     * @param repositoryId 数据源ID
     * @return 数据库概要信息
     */
    DatabaseSummary getDatabaseSummary(Integer repositoryId);
    
    /**
     * 获取所有数据源的概要信息
     * @return 数据源概要信息列表
     */
    List<DatabaseSummary> getAllDatabaseSummaries();
    
    /**
     * 测试数据库连接
     * @param repositoryId 数据源ID
     * @return 连接状态 (true:成功 false:失败)
     */
    Boolean testConnection(Integer repositoryId);
    
    /**
     * 查询表数据（分页）
     * @param request 查询请求
     * @return 分页数据
     */
    IPage<LinkedHashMap<String, Object>> getTableData(TableDataQueryRequest request);
    
    /**
     * 获取表的记录总数
     * @param repositoryId 数据源ID
     * @param schemaName Schema名称
     * @param tableName 表名
     * @return 记录总数
     */
    Long getTableRowCount(Integer repositoryId, String schemaName, String tableName);
    
    /**
     * 获取数据库运行状态信息
     * @param repositoryId 数据源ID
     * @return 运行状态信息
     */
    LinkedHashMap<String, Object> getDatabaseStatus(Integer repositoryId);
}