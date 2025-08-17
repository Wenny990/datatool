package com.wnhuang.datax.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wnhuang.common.domain.entity.BaseTableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * datax任务
 * @TableName datax_job
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="datax_job")
@Data
public class DataxJob extends BaseTableField implements Serializable  {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 配置对象
     */
    private String setting;

    /**
     * 分组ID
     */
    private Integer groupId;
    /**
     * 输入对象
     */
    private String reader;

    /**
     * 输出对象
     */
    private String writer;

    /**
     * 转换对象
     */
    private String transformer;

    /**
     * 任务参数
     */
    private String jobParams;

    /**
     * 服务器id
     */
    private Integer serverId;

    /**
     * json路径
     */
    private String jsonPath;

    /**
     * JVM启动参数
     */
    private String jvmParams;

    private String splitParams;

    /**
     * 每次执行任务时，任务标识
     */
    @TableField(exist = false)
    private String uuid;

    /**
     * 每次执行任务时，日志文件的路径
     */
    @TableField(exist = false)
    private String logPath;

    /**
     * 分段执行时，当前段的where条件
     */
    @TableField(exist = false)
    private String currWhereStr;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}