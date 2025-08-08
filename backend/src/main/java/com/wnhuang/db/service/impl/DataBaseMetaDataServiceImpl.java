package com.wnhuang.db.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.wnhuang.common.aviator.AviatorUtils;
import com.wnhuang.common.domain.entity.RepositoryConfig;
import com.wnhuang.common.domain.entity.RepositorySource;
import com.wnhuang.common.service.RepositoryConfigService;
import com.wnhuang.common.service.RepositorySourceService;
import com.wnhuang.db.domain.entity.*;
import com.wnhuang.db.domain.request.DbSchemaMetaRequest;
import com.wnhuang.db.domain.request.DbTableMetaRequest;
import com.wnhuang.db.mapper.DataBaseMetaDataMapper;
import com.wnhuang.db.service.DataBaseMetaDataService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 数据库元数据服务实现类
 *
 * @author wnhuang
 * @date 2024/11/17 16:01
 */
@Service
@Slf4j
public class DataBaseMetaDataServiceImpl implements DataBaseMetaDataService {

    @Autowired
    RepositoryConfigService repositoryConfigService;

    @Autowired
    DataBaseMetaDataMapper dataBaseMetaDataMapper;

    @Autowired
    RepositorySourceService repositorySourceService;

    @Autowired
    DataSource dataSource;

    /**
     * 模板方法：执行数据库元数据操作
     */
    private <T> List<T> executeMetaDataOperation(String repositoryKey, Function<Connection, List<T>> operation) {
        if (repositoryKey == null) {
            throw new IllegalArgumentException("repositoryId 不能为空");
        }

        repositorySourceService.changeDataSource(repositoryKey);
        DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;
        DataSource currentDataSource = dynamicRoutingDataSource.getDataSources().get(repositoryKey);

        if (currentDataSource == null) {
            throw new RuntimeException("未找到数据源: " + repositoryKey);
        }

        Connection connection = null;
        try {
            connection = currentDataSource.getConnection();
            return operation.apply(connection);
        } catch (SQLException e) {
            log.error("执行数据库操作失败, repositoryKey: {}", repositoryKey, e);
            throw new RuntimeException("数据库操作失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("执行数据库操作时发生未知错误, repositoryKey: {}", repositoryKey, e);
            throw new RuntimeException("操作失败: " + e.getMessage(), e);
        } finally {
            closeConnection(connection);
            repositorySourceService.resetRepository();
        }
    }

    /**
     * 安全关闭数据库连接
     */
    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.warn("关闭数据库连接失败", e);
            }
        }
    }

    /**
     * 获取数据库列表
     *
     * @param repositoryId 数据库配置表主键
     */
    @Override
    public List<SchemaInfo> getSchemaList(Integer repositoryId) {
        try {
            RepositorySource repositorySource = repositorySourceService.getById(repositoryId);
            RepositoryConfig repositoryConfig = repositoryConfigService.getById(repositorySource.getDataProviderType());
            repositorySourceService.changeDataSource(String.valueOf(repositoryId));
            return dataBaseMetaDataMapper.getSchemaList(repositoryConfig.getSchemaSql());
        } finally {
            repositorySourceService.resetRepository();
        }
    }

    @NotNull
    private List<SchemaInfo> getSchemaInfos(Connection connection) {
        DatabaseMetaData metaData = null;
        ResultSet schemas = null;
        List<SchemaInfo> schemaInfoList = new ArrayList<>();
        try {
            metaData = connection.getMetaData();
            schemas = metaData.getSchemas();
            while (schemas.next()) {
                SchemaInfo schemaInfo = new SchemaInfo();
                String schemaName = getColumnValue(schemas, "TABLE_SCHEM", "SCHEMA_NAME", "schema_name");
                String catalogName = getColumnValue(schemas, "TABLE_CATALOG", "CATALOG_NAME", "catalog_name");
                schemaInfo.setSchemaName(schemaName);
                schemaInfo.setCatalogName(catalogName);
                schemaInfoList.add(schemaInfo);
            }
        } catch (SQLException e) {
            log.error("获取数据库列表失败, ", e);
            throw new RuntimeException(e);
        }
        return schemaInfoList;
    }

    /**
     * 获取数据库表列表
     *
     * @param dbSchemaMetaRequest 数据库配置
     */
    @Override
    public List<TableInfo> getTableList(DbSchemaMetaRequest dbSchemaMetaRequest) {
        try {
            Integer repositoryId = dbSchemaMetaRequest.getRepositoryId();
            RepositorySource repositorySource = repositorySourceService.getById(repositoryId);
            RepositoryConfig repositoryConfig = repositoryConfigService.getById(repositorySource.getDataProviderType());
            String tableSql = repositoryConfig.getTableSql();
            tableSql = AviatorUtils.compileSql(tableSql, JSONUtil.parseObj(dbSchemaMetaRequest));
            repositorySourceService.changeDataSource(String.valueOf(repositoryId));
            return dataBaseMetaDataMapper.getTableList(tableSql);
        } finally {
            repositorySourceService.resetRepository();
        }
    }

    private List<TableInfo> getTableInfos(Connection connection, DbSchemaMetaRequest dbSchemaMetaRequest) {
        if (dbSchemaMetaRequest.getSchemaName() == null) {
            throw new IllegalArgumentException("schemaName 不能为空");
        }
        DatabaseMetaData metaData = null;
        ResultSet tables = null;
        List<TableInfo> tableInfoList = new ArrayList<>();
        try {
            metaData = connection.getMetaData();
            tables = metaData.getTables(
                    null,
                    dbSchemaMetaRequest.getSchemaName(),
                    null,
                    new String[]{"TABLE"}
            );
            while (tables.next()) {
                TableInfo tableInfo = new TableInfo();
                tableInfo.setCatalogName(getStringValue(tables, "TABLE_CAT"));
                tableInfo.setSchemaName(getStringValue(tables, "TABLE_SCHEM"));
                tableInfo.setTableName(getStringValue(tables, "TABLE_NAME"));
                tableInfo.setTableType(getStringValue(tables, "TABLE_TYPE"));
                tableInfo.setRemarks(getStringValue(tables, "REMARKS"));
                tableInfoList.add(tableInfo);
            }
        } catch (SQLException e) {
            log.error("获取数据库表列表失败, ", e);
            throw new RuntimeException(e);
        }
        return tableInfoList;
    }

    /**
     * 获取数据库表字段列表
     *
     * @param dbTableMetaRequest
     * @return
     */
    @Override
    public List<TableColumnInfo> getTableColumnList(DbTableMetaRequest dbTableMetaRequest) {
        try {
            Integer repositoryId = dbTableMetaRequest.getRepositoryId();
            RepositorySource repositorySource = repositorySourceService.getById(repositoryId);
            RepositoryConfig repositoryConfig = repositoryConfigService.getById(repositorySource.getDataProviderType());
            String columnSql = repositoryConfig.getColumnSql();
            columnSql = AviatorUtils.compileSql(columnSql, JSONUtil.parseObj(dbTableMetaRequest));
            repositorySourceService.changeDataSource(String.valueOf(repositoryId));
            return dataBaseMetaDataMapper.getTableColumnList(columnSql);
        } finally {
            repositorySourceService.resetRepository();
        }

    }

    /**
     * 获取数据库表索引列表
     *
     * @param dbTableMetaRequest
     * @return
     */
    @Override
    public List<TableIndex> getTableIndexList(DbTableMetaRequest dbTableMetaRequest) {
        try {
            Integer repositoryId = dbTableMetaRequest.getRepositoryId();
            RepositorySource repositorySource = repositorySourceService.getById(repositoryId);
            RepositoryConfig repositoryConfig = repositoryConfigService.getById(repositorySource.getDataProviderType());
            String indexSql = repositoryConfig.getIndexSql();
            indexSql = AviatorUtils.compileSql(indexSql, JSONUtil.parseObj(dbTableMetaRequest));
            repositorySourceService.changeDataSource(String.valueOf(repositoryId));
            List<TableIndex> tableIndexList = dataBaseMetaDataMapper.getTableIndexList(indexSql);
            return transformTableIndex(tableIndexList);
        } finally {
            repositorySourceService.resetRepository();
        }
    }

    private List<TableIndex> transformTableIndex(List<TableIndex> tableIndexList) {
        List<TableIndex> list = new ArrayList<>();
        for (TableIndex tableIndex : tableIndexList) {
            // 防止空指针异常
            if (tableIndex.getIndexName() == null) {
                continue;
            }

            TableIndex index = list.stream()
                    .filter(t -> tableIndex.getIndexName().equals(t.getIndexName()))
                    .findFirst()
                    .orElse(null);

            if (index != null) {
                // 防止空指针异常
                if (index.getIndexColumns() != null && tableIndex.getColumnName() != null) {
                    index.getIndexColumns().add(
                            TableIndexColumn.builder()
                                    .columnName(tableIndex.getColumnName())
                                    .ordinalPosition(tableIndex.getOrdinalPosition())
                                    .build());
                }
            } else {
                List<TableIndexColumn> indexColumns = new ArrayList<>();
                if (tableIndex.getColumnName() != null) {
                    indexColumns.add(TableIndexColumn.builder()
                            .columnName(tableIndex.getColumnName())
                            .ordinalPosition(tableIndex.getOrdinalPosition())
                            .build());
                }
                tableIndex.setIndexColumns(indexColumns);
                list.add(tableIndex);
            }
        }
        return list;
    }

    // 辅助方法：尝试多个可能的列名获取值
    private String getColumnValue(ResultSet rs, String... possibleColumnNames) throws SQLException {
        for (String columnName : possibleColumnNames) {
            try {
                return rs.getString(columnName);
            } catch (SQLException e) {
                continue;
            }
        }
        return null;
    }

    // 辅助方法：安全获取字符串值
    private String getStringValue(ResultSet rs, String columnName) {
        try {
            return rs.getString(columnName);
        } catch (SQLException e) {
            log.warn("获取列 {} 值失败", columnName, e);
            return null;
        }
    }
}
