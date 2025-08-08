package com.wnhuang.common.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.io.Serializable;

/**
 * 数据仓库
 *
 * @TableName repository_source
 */
@TableName(value = "repository_source")
@Data
public class RepositorySource extends BaseTableField implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 数据库名
     */
    private String repositoryName;

    /**
     * 连接字符串
     */
    private String url;

    /**
     * ip地址
     */
    private String host;
    /**
     * 端口号
     */
    private Integer port;
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 数据库类型
     */
    private Integer dataProviderType;

    /**
     * 数据库类型名称
     */
    private String dataSourceName;

    /**
     * 驱动
     */
    private String driverClassName;

    /**
     * 读取器
     */
    private String reader;

    /**
     * 写入器
     */
    private String writer;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
