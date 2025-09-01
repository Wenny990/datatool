package com.wnhuang.db.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wnhuang.common.domain.entity.RepositorySource;
import com.wnhuang.common.service.RepositorySourceService;
import com.wnhuang.db.domain.entity.DatabaseSummary;
import com.wnhuang.db.domain.request.TableDataQueryRequest;
import com.wnhuang.db.service.DataBaseMetaDataService;
import com.wnhuang.db.service.DatabaseMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 数据库监控服务实现类
 * @author wnhuang
 * @date 2024/12/01
 */
@Service
@Slf4j
public class DatabaseMonitorServiceImpl implements DatabaseMonitorService {
    
    @Autowired
    private RepositorySourceService repositorySourceService;
    
    @Autowired
    private DataBaseMetaDataService dataBaseMetaDataService;
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public DatabaseSummary getDatabaseSummary(Integer repositoryId) {
        DatabaseSummary summary = new DatabaseSummary();
        summary.setRepositoryId(repositoryId);
        summary.setLastCheckTime(LocalDateTime.now());
        
        try {
            // 获取数据源信息
            RepositorySource repositorySource = repositorySourceService.getById(repositoryId);
            if (repositorySource == null) {
                summary.setConnectionStatus(0);
                summary.setErrorMessage("数据源不存在");
                return summary;
            }
            
            summary.setRepositoryName(repositorySource.getName());
            summary.setDatabaseUrl(repositorySource.getUrl());
            summary.setDatabaseType(getDatabaseType(repositorySource.getUrl()));
            
            // 切换到目标数据源
            repositorySourceService.changeDataSource(String.valueOf(repositoryId));
            DynamicRoutingDataSource dynamicDataSource = (DynamicRoutingDataSource) dataSource;
            DataSource targetDataSource = dynamicDataSource.getDataSources().get(String.valueOf(repositoryId));
            
            if (targetDataSource == null) {
                summary.setConnectionStatus(0);
                summary.setErrorMessage("数据源连接失败");
                return summary;
            }
            
            try (Connection connection = targetDataSource.getConnection()) {
                // 基本连接信息
                DatabaseMetaData metaData = connection.getMetaData();
                summary.setDatabaseType(metaData.getDatabaseProductName());
                summary.setDatabaseVersion(metaData.getDatabaseProductVersion());
                summary.setConnectionStatus(1);
                int catalogCount = 0;
                ResultSet catalogs = metaData.getCatalogs();
                while (catalogs.next()){
                    catalogCount++;
                }
                summary.setCatalogCount(catalogCount);
                // 统计Schema数量
                ResultSet schemas = metaData.getSchemas(null, null);
                int schemaCount = 0;
                while (schemas.next()){
                    schemaCount++;
                }

                summary.setSchemaCount(schemaCount);
                
                // 统计表数量
                int totalTables = 0;
                int totalViews = 0;
                ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE", "VIEW"});
                while (tables.next()) {
                    String tableType = tables.getString("TABLE_TYPE");
                    if ("TABLE".equalsIgnoreCase(tableType)) {
                        totalTables++;
                    } else if ("VIEW".equalsIgnoreCase(tableType)) {
                        totalViews++;
                    }
                }
                summary.setTableCount(totalTables);
                summary.setViewCount(totalViews);
                
                // 获取数据库状态信息
                try {
                    LinkedHashMap<String, Object> statusInfo = getDatabaseStatus(connection);
                    if (statusInfo.containsKey("maxConnections")) {
                        summary.setMaxConnections(Integer.valueOf(statusInfo.get("maxConnections").toString()));
                    }
                    if (statusInfo.containsKey("currentConnections")) {
                        summary.setCurrentConnections(Integer.valueOf(statusInfo.get("currentConnections").toString()));
                    }
                    if (statusInfo.containsKey("uptime")) {
                        summary.setUptime(Long.valueOf(statusInfo.get("uptime").toString()));
                    }

                } catch (Exception e) {
                    log.warn("获取数据库状态信息失败: {}", e.getMessage());
                }

                summary.setDatabaseSize(dataBaseMetaDataService.getDbSize(repositoryId));
                
            } catch (SQLException e) {
                log.error("获取数据库概要信息失败: {}", e.getMessage(), e);
                summary.setConnectionStatus(0);
                summary.setErrorMessage("数据库连接异常: " + e.getMessage());
            }
            
        } catch (Exception e) {
            log.error("获取数据源信息失败: {}", e.getMessage(), e);
            summary.setConnectionStatus(0);
            summary.setErrorMessage("获取数据源信息失败: " + e.getMessage());
        } finally {
            repositorySourceService.resetRepository();
        }
        
        return summary;
    }
    
    @Override
    public List<DatabaseSummary> getAllDatabaseSummaries() {
        List<DatabaseSummary> summaries = new ArrayList<>();
        
        try {
            List<RepositorySource> repositorySources = repositorySourceService.list();
            for (RepositorySource source : repositorySources) {
                DatabaseSummary summary = getDatabaseSummary(source.getId());
                summaries.add(summary);
            }
        } catch (Exception e) {
            log.error("获取所有数据源概要信息失败: {}", e.getMessage(), e);
        }
        
        return summaries;
    }
    
    @Override
    public Boolean testConnection(Integer repositoryId) {
        try {
            repositorySourceService.changeDataSource(String.valueOf(repositoryId));
            DynamicRoutingDataSource dynamicDataSource = (DynamicRoutingDataSource) dataSource;
            DataSource targetDataSource = dynamicDataSource.getDataSources().get(String.valueOf(repositoryId));
            
            if (targetDataSource == null) {
                return false;
            }
            
            try (Connection connection = targetDataSource.getConnection()) {
                return connection.isValid(5); // 5秒超时
            }
        } catch (Exception e) {
            log.error("测试数据库连接失败: {}", e.getMessage(), e);
            return false;
        } finally {
            repositorySourceService.resetRepository();
        }
    }
    
    @Override
    public IPage<LinkedHashMap<String, Object>> getTableData(TableDataQueryRequest request) {
        try {
            // 构建查询SQL
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ");
            
            if (StrUtil.isNotBlank(request.getSchemaName())) {
                sql.append(request.getSchemaName()).append(".");
            }
            sql.append(request.getTableName());
            
            // 添加WHERE条件
            if (StrUtil.isNotBlank(request.getWhereClause())) {
                sql.append(" WHERE ").append(request.getWhereClause());
            }
            
            // 添加排序
            if (StrUtil.isNotBlank(request.getOrderBy())) {
                sql.append(" ORDER BY ").append(request.getOrderBy())
                   .append(" ").append(request.getOrderDirection());
            }
            
            // 创建分页对象
            Page<LinkedHashMap<String, Object>> page = new Page<>(request.getCurrent(), request.getPageSize());
            
            // 执行分页查询
            return repositorySourceService.execPageQuery(String.valueOf(request.getRepositoryId()), page, sql.toString());
            
        } catch (Exception e) {
            log.error("查询表数据失败: {}", e.getMessage(), e);
            throw new RuntimeException("查询表数据失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Long getTableRowCount(Integer repositoryId, String schemaName, String tableName) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) as count FROM ");
            
            if (StrUtil.isNotBlank(schemaName)) {
                sql.append(schemaName).append(".");
            }
            sql.append(tableName);
            
            return repositorySourceService.getTotalBySql(String.valueOf(repositoryId), sql.toString());
            
        } catch (Exception e) {
            log.error("获取表记录数失败: {}", e.getMessage(), e);
            return 0L;
        }
    }
    
    @Override
    public LinkedHashMap<String, Object> getDatabaseStatus(Integer repositoryId) {
        try {
            repositorySourceService.changeDataSource(String.valueOf(repositoryId));
            DynamicRoutingDataSource dynamicDataSource = (DynamicRoutingDataSource) dataSource;
            DataSource targetDataSource = dynamicDataSource.getDataSources().get(String.valueOf(repositoryId));
            
            if (targetDataSource != null) {
                try (Connection connection = targetDataSource.getConnection()) {
                    return getDatabaseStatus(connection);
                }
            }
        } catch (Exception e) {
            log.error("获取数据库状态失败: {}", e.getMessage(), e);
        } finally {
            repositorySourceService.resetRepository();
        }
        
        return new LinkedHashMap<>();
    }
    
    /**
     * 根据数据库URL判断数据库类型
     */
    private String getDatabaseType(String url) {
        if (url == null) return "Unknown";
        
        url = url.toLowerCase();
        if (url.contains("mysql")) return "MySQL";
        if (url.contains("postgresql")) return "PostgreSQL";
        if (url.contains("oracle")) return "Oracle";
        if (url.contains("sqlserver")) return "SQL Server";
        if (url.contains("h2")) return "H2";
        if (url.contains("sqlite")) return "SQLite";
        
        return "Unknown";
    }
    
    /**
     * 获取数据库状态信息
     */
    private LinkedHashMap<String, Object> getDatabaseStatus(Connection connection) throws SQLException {
        LinkedHashMap<String, Object> status = new LinkedHashMap<>();
        DatabaseMetaData metaData = connection.getMetaData();
        String databaseType = getDatabaseType(metaData.getURL());
        
        try {
            if ("MySQL".equals(databaseType) || "OceanBase".equals(databaseType)) {
                getMySQLStatus(connection, status);
            } else if ("PostgreSQL".equals(databaseType)) {
                getPostgreSQLStatus(connection, status);
            } else if ("Oracle".equals(databaseType)) {
                getOracleStatus(connection, status);
            }
        } catch (Exception e) {
            log.warn("获取数据库状态信息失败: {}", e.getMessage());
        }
        
        return status;
    }
    

    /**
     * 获取MySQL状态信息（扩展版本）
     */
    private void getMySQLStatus(Connection connection, LinkedHashMap<String, Object> status) throws SQLException {
        // 原有查询...
        // 最大连接数
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW VARIABLES LIKE 'max_connections'")) {
            if (rs.next()) {
                status.put("maxConnections", rs.getInt("Value"));
            }
        }

        // 当前连接数
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW STATUS LIKE 'Threads_connected'")) {
            if (rs.next()) {
                status.put("currentConnections", rs.getInt("Value"));
            }
        }

        // 运行时间
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW STATUS LIKE 'Uptime'")) {
            if (rs.next()) {
                status.put("uptime", rs.getLong("Value"));
            }
        }

        // 数据库大小
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS size_mb FROM information_schema.tables")) {
            if (rs.next()) {
                status.put("databaseSize", rs.getLong("size_mb"));
            }
        }

        // 扩展查询 - 更多参数
        // 1. 查询所有重要的系统变量
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW VARIABLES WHERE Variable_name IN ('innodb_buffer_pool_size', 'max_allowed_packet', 'innodb_log_file_size', 'innodb_log_buffer_size', 'query_cache_size', 'tmp_table_size', 'max_heap_table_size', 'table_open_cache', 'thread_cache_size', 'innodb_flush_log_at_trx_commit', 'sync_binlog')")) {
            while (rs.next()) {
                status.put(rs.getString("Variable_name"), rs.getString("Value"));
            }
        }

        // 2. 查询重要的运行状态
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW STATUS WHERE Variable_name IN ('Threads_running', 'Threads_cached', 'Connections', 'Aborted_connects', 'Created_tmp_tables', 'Created_tmp_disk_tables', 'Key_reads', 'Key_read_requests', 'Key_writes', 'Key_write_requests', 'Innodb_buffer_pool_reads', 'Innodb_buffer_pool_read_requests', 'Qcache_hits', 'Qcache_inserts', 'Bytes_received', 'Bytes_sent')")) {
            while (rs.next()) {
                status.put(rs.getString("Variable_name"), rs.getString("Value"));
            }
        }

        // 3. 查询数据库数量和表数量统计
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as db_count FROM information_schema.SCHEMATA WHERE SCHEMA_NAME NOT IN ('information_schema', 'performance_schema', 'mysql', 'sys')")) {
            if (rs.next()) {
                status.put("databaseCount", rs.getInt("db_count"));
            }
        }

        // 4. 查询所有表的数量
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as table_count FROM information_schema.TABLES WHERE TABLE_SCHEMA NOT IN ('information_schema', 'performance_schema', 'mysql', 'sys')")) {
            if (rs.next()) {
                status.put("totalTableCount", rs.getInt("table_count"));
            }
        }

        // 5. 获取InnoDB引擎状态
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW ENGINE INNODB STATUS")) {
            if (rs.next()) {
                // 可以解析更多InnoDB详细信息
                status.put("innodbStatus", "Available");
            }
        } catch (Exception e) {
            // 忽略错误，某些权限下可能无法执行
        }
    }

    /**
     * 获取PostgreSQL状态信息（扩展版本）
     */
    private void getPostgreSQLStatus(Connection connection, LinkedHashMap<String, Object> status) throws SQLException {
        // 原有查询...
        // 最大连接数
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW max_connections")) {
            if (rs.next()) {
                status.put("maxConnections", rs.getInt(1));
            }
        }

        // 当前连接数
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT count(*) FROM pg_stat_activity")) {
            if (rs.next()) {
                status.put("currentConnections", rs.getInt(1));
            }
        }

        // 数据库大小
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT pg_size_pretty(pg_database_size(current_database()))")) {
            if (rs.next()) {
                String sizeStr = rs.getString(1);
                // 简单解析大小字符串，转换为MB
                status.put("databaseSize", parseSizeToMB(sizeStr));
            }
        }

        // 扩展查询 - 更多PostgreSQL参数

        // 1. 查询共享缓冲区大小
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW shared_buffers")) {
            if (rs.next()) {
                status.put("shared_buffers", rs.getString(1));
            }
        }

        // 2. 查询工作内存大小
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW work_mem")) {
            if (rs.next()) {
                status.put("work_mem", rs.getString(1));
            }
        }

        // 3. 查询维护工作内存大小
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW maintenance_work_mem")) {
            if (rs.next()) {
                status.put("maintenance_work_mem", rs.getString(1));
            }
        }

        // 4. 查询有效缓存大小
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW effective_cache_size")) {
            if (rs.next()) {
                status.put("effective_cache_size", rs.getString(1));
            }
        }

        // 5. 查询检查点间隔
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW checkpoint_segments")) {
            if (rs.next()) {
                status.put("checkpoint_segments", rs.getString(1));
            }
        }

        // 6. 查询WAL缓冲区大小
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW wal_buffers")) {
            if (rs.next()) {
                status.put("wal_buffers", rs.getString(1));
            }
        }

        // 7. 查询数据库版本
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW server_version")) {
            if (rs.next()) {
                status.put("server_version", rs.getString(1));
            }
        }

        // 8. 查询当前数据库统计信息
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT datname, pg_size_pretty(pg_database_size(datname)) as size FROM pg_database WHERE datistemplate = false")) {
            List<String> dbInfo = new ArrayList<>();
            while (rs.next()) {
                dbInfo.add(rs.getString("datname") + ": " + rs.getString("size"));
            }
            status.put("databaseDetails", dbInfo);
        }

        // 9. 查询活动连接详细信息
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT datname, usename, application_name, client_addr, state FROM pg_stat_activity WHERE state IS NOT NULL")) {
            List<String> connectionDetails = new ArrayList<>();
            while (rs.next()) {
                connectionDetails.add(
                        "DB: " + rs.getString("datname") +
                                ", User: " + rs.getString("usename") +
                                ", App: " + rs.getString("application_name") +
                                ", Client: " + rs.getString("client_addr") +
                                ", State: " + rs.getString("state")
                );
            }
            status.put("activeConnectionsDetails", connectionDetails);
        }

        // 10. 查询表空间信息
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT spcname, pg_size_pretty(pg_tablespace_size(spcname)) as size FROM pg_tablespace")) {
            List<String> tablespaceInfo = new ArrayList<>();
            while (rs.next()) {
                tablespaceInfo.add(rs.getString("spcname") + ": " + rs.getString("size"));
            }
            status.put("tablespaceInfo", tablespaceInfo);
        }
    }
    
    /**
     * 获取Oracle状态信息
     */
    private void getOracleStatus(Connection connection, LinkedHashMap<String, Object> status) throws SQLException {
        // 当前连接数
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT count(*) FROM v$session WHERE status = 'ACTIVE'")) {
            if (rs.next()) {
                status.put("currentConnections", rs.getInt(1));
            }
        }
        
        // 数据库大小
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ROUND(SUM(bytes)/1024/1024, 2) as size_mb FROM dba_data_files")) {
            if (rs.next()) {
                status.put("databaseSize", rs.getLong("size_mb"));
            }
        }
    }
    
    /**
     * 解析大小字符串为MB
     */
    private long parseSizeToMB(String sizeStr) {
        if (sizeStr == null) return 0;
        
        sizeStr = sizeStr.toLowerCase();
        try {
            if (sizeStr.contains("mb")) {
                return Long.parseLong(sizeStr.replaceAll("[^0-9]", ""));
            } else if (sizeStr.contains("gb")) {
                return Long.parseLong(sizeStr.replaceAll("[^0-9]", "")) * 1024;
            } else if (sizeStr.contains("kb")) {
                return Long.parseLong(sizeStr.replaceAll("[^0-9]", "")) / 1024;
            }
        } catch (NumberFormatException e) {
            log.warn("解析数据库大小失败: {}", sizeStr);
        }
        
        return 0;
    }
}