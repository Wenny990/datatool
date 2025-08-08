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
 * datax任务组
 * @TableName datax_job_group
 */
@TableName(value ="datax_job_group")
@Data
public class DataxJobGroup extends BaseTableField implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 组名称
     */
    private String groupName;

    /**
     * 是否启用
     */
    private Integer isEnable;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}