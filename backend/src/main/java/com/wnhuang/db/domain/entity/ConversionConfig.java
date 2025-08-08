package com.wnhuang.db.domain.entity;

import lombok.Data;

import java.util.List;

/**
 * @author wnhuang
 * @date 2025/6/16 00:36
 */
@Data
public class ConversionConfig {

    /**
     * 源端类型
     */
    private Integer sourceDbType;

    /**
     * 目标端类型
     */
    private Integer targetDbType;

    /**
     * 0: 默认不变换
     * 1: 全大写
     * 2: 全小写
     */
    private Integer caseType;

    /**
     * 添加表名前缀
     */
    private String prefix;

    /**
     * 添加表名后缀
     */
    private String suffix;

    /**
     * 忽略主键
     */
    private Boolean ignorePrimaryKey;

    /**
     * 数据库分隔符
     */
    private String dbSeparator;

    /**
     * 额外的列
     */
    private List<TableColumnInfo> extraColumns;
}
