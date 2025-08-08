package com.wnhuang.db.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表字段信息
 * @author wnhuang
 * @date 2024/11/17 15:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableColumnInfo {

    /**
     * 数据库
     */
    private String catalogName;

    /**
     * 数据库模式名
     */
    private String schemaName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 字段ID
     */
    private Integer columnId;

    /**
     * 字段名
     */
    private String columnName;

    /**
     * 字段注释
     */
    private String remarks;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 数据类型ID
     */
    private Integer dataTypeId;

    /**
     * 数据类型长度
     */
    private String dataLength;

    /**
     * 数据类型精度，整数部分
     */
    private Integer dataPrecision;

    /**
     * 数据类型精度，小数部分
     */
    private Integer dataScale;

    /**
     * 是否允许为空
     */
    private Boolean nullable;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否主键
     */
    private Boolean isPrimaryKey;
}
