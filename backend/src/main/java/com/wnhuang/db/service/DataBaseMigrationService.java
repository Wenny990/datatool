package com.wnhuang.db.service;


import com.wnhuang.db.domain.entity.ConversionConfig;
import com.wnhuang.db.domain.entity.TableColumnInfo;
import com.wnhuang.db.domain.entity.TableIndex;
import com.wnhuang.db.domain.entity.TableInfo;

import java.util.List;

/**
 * 数据库迁移服务
 * @author wnhuang
 * @date 2025/6/16 00:31
 */
public interface DataBaseMigrationService {

    // 获取迁移SQL
    String getMigrationSql(TableInfo tableInfo, List<TableColumnInfo> columnInfoList, TableIndex tableIndex, ConversionConfig conversionConfig);

    List<TableColumnInfo> getConversionColumn(List<TableColumnInfo> columnInfoList, ConversionConfig conversionConfig);
}
