package com.wnhuang.db.domain.entity;

import lombok.Data;

import java.util.Date;

/**
 * 数据库模式信息
 * @author wnhuang
 * @date 2024/11/17 15:39
 */
@Data
public class SchemaInfo {

    /**
     * 数据库名
     */
    private String catalogName;

    /**
     * 数据库模式名称
     */
    private String schemaName;


}
