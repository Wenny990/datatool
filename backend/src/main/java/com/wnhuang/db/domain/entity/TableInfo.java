package com.wnhuang.db.domain.entity;

import lombok.Data;

import java.util.Date;

/**
 * 数据库表信息
 * @author wnhuang
 * @date 2024/11/17 15:43
 */
@Data
public class TableInfo {

    private String catalogName;

    private String schemaName;

    private String tableName;

    private String tableType;

    private String remarks;

    private Date createTime;

}
