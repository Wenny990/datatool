package com.wnhuang.common.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息表
 * @TableName system_user
 */
@TableName(value ="system_user")
@Data
public class SystemUser extends BaseTableField implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 工号
     */
    private String userCode;

    /**
     * 密码
     */
    private String Password;

    /**
     * 姓名
     */
    private String userName;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 启用状态，1-启用，0-禁用
     */
    private Boolean status;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}
