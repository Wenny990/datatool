package com.wnhuang.common.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author Administrator
 * @TableName repository_config
 */

@TableName(value ="repository_config")
@Data
public class RepositoryConfig extends BaseTableField implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Integer id;

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 获取表空间SQL
     */
    private String schemaSql;

    /**
     * 获取表SQL
     */
    private String tableSql;

    /**
     * 获取表字段SQL
     */
    private String columnSql;

    /**
     * 获取表主键SQL
     */
    private String primaryKeySql;

    /**
     * 获取数据库版本信息SQL
     */
    private String versionSql;

    /**
     * 获取表索引SQL
     */
    private String indexSql;

    private String dbDriver;

    private String dbUrl;

    private String dbPort;

    private String identifierBefore;

    private String identifierAfter;

    private String ddlTemplate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
