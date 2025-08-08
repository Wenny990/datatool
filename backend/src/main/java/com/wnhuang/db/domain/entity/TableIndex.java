package com.wnhuang.db.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 表索引
 *
 * @author wnhuang
 * @date 2024/11/17 15:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableIndex {

    /*
     * 库名
     */
    private String catalogName;

    /*
     * 模式名
     */
    private String schemaName;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 索引名称
     */
    private String indexName;

    /**
     * 索引类型
     */
    private short type;

    /**
     * 是否唯一索引
     */
    private Boolean isUnique;

    /**
     * 是否升序
     */
    private Boolean isAsc;

    /**
     * 索引列
     */
    private List<TableIndexColumn> indexColumns;

    private String columnName;

    private short ordinalPosition;

    private String indexQualifier;

    private long cardinality;

    private long pages;

    private String filterCondition;


}
