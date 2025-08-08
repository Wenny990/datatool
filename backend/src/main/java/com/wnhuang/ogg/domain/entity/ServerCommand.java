package com.wnhuang.ogg.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 命令表配置
 * @TableName server_command
 */
@TableName(value ="server_command")
@Data
public class ServerCommand implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 类型，1-查看ogg进程列表，2-查看ogg进程信息，3-查看ogg进程详情，4-启动进程，5-停止进程，6-查看进程参数，7-编辑进程参数
     */
    private String type;

    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 执行命令详情
     */
    private String command;

    /**
     * 操作系统，01-windows,02-linux
     */
    private String serverOs;

    /**
     * 
     */
    private Integer createUser;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Integer updateUser;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}