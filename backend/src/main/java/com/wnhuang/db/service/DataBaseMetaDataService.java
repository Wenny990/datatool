package com.wnhuang.db.service;

import com.wnhuang.db.domain.entity.SchemaInfo;
import com.wnhuang.db.domain.entity.TableColumnInfo;
import com.wnhuang.db.domain.entity.TableIndex;
import com.wnhuang.db.domain.entity.TableInfo;
import com.wnhuang.db.domain.request.DbSchemaMetaRequest;
import com.wnhuang.db.domain.request.DbTableMetaRequest;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 数据库元数据服务
 * @author wnhuang
 * @date 2024/11/17 15:34
 */
public interface DataBaseMetaDataService {

    @Cacheable(key = "'dbsize_' + #repositoryId")
    String getDbSize(Integer repositoryId);

    /**
     * 获取数据库schema列表
     *
     * @param repositoryId 数据库配置表主键
     */
    List<SchemaInfo> getSchemaList(Integer repositoryId);

    /**
     * 获取数据库table列表
     *
     * @param dbSchemaMetaRequest
     */
    List<TableInfo> getTableList(DbSchemaMetaRequest dbSchemaMetaRequest);

    /**
     * 获取数据库表字段列表
     * @param dbTableMetaRequest
     * @return
     */
    List<TableColumnInfo> getTableColumnList(DbTableMetaRequest dbTableMetaRequest);

    /**
     * 获取数据库表索引列表
     * @param dbTableMetaRequest
     * @return
     */
    List<TableIndex> getTableIndexList(DbTableMetaRequest dbTableMetaRequest);
}
