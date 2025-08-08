package com.wnhuang.common.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 服务器监控表
 *
 * @TableName monitor_server_info
 */
@TableName(value = "monitor_server_info")
@Data
public class MonitorServerInfo extends BaseTableField implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 服务器ip地址
     */
    private String serverIp;

    /**
     * ogg,kafka,hadoop,db,cdh
     */
    private String serverType;

    /**
     * 监控端口
     */
    private String serverPort;

    /**
     * 服务器名称
     */
    private String serverName;

    /**
     * 服务器系统 01-window 02-linux 03-unix
     */
    private String serverOs;

    /**
     * 服务器描述
     */
    private String serverDesc;

    /**
     * 是否可用（0-不可  1 -正常）
     */
    private Boolean status;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 服务器性能监控 0-不监控  1-监控
     */
    private Boolean serverStatus;

    /**
     * 服务器用户名
     */
    private String serverUser;

    /**
     * 服务器密码
     */
    private String serverPass;

    /**
     * 0-否  1-是（是否主页监控展示）
     */
    private String isShowHome;

    /**
     * 运行命令路径
     */
    private String cmdPath;

    /**
     * ogg路径
     */
    private String oggPath;
    /**
     * SSH端口
     */
    private Integer sshPort;


    private String characterSet;


    private Integer repositorySourceId;

    /**
     * 当前服务器是否已经上传过datax的配置文件
     */
    private Boolean isUploaded;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}