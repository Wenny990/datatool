package com.wnhuang.common.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * @author wnhuang
 * @Package com.base.domain
 * @date 2022-07-28 21:03
 */
@Data
public class BaseTableField {

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @OrderBy(asc = true)
    public Date createTime;

    /**
     * 创建用户
     */
    @TableField(fill = FieldFill.INSERT)
    public Integer createUser;

    /**
     * 最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    public Date updateTime;

    /**
     * 最后更新用户
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    public Integer updateUser;

    /**
     * 是否删除
     */
    public Boolean isDelete;
}
