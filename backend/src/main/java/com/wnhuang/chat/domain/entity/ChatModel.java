package com.wnhuang.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * ai模型
 * @TableName chat_model
 */
@TableName(value ="chat_model")
@Data
public class ChatModel {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 模型编码
     */
    private String modelCode;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型描述
     */
    private String modelDesc;

    /**
     * 所属平台
     */
    private String modelGroup;

    /**
     * 上下文数量
     */
    private Integer tokenNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 
     */
    private Integer createUser;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private Date updateUser;

    /**
     * 
     */
    private Integer isDelete;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ChatModel other = (ChatModel) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getModelCode() == null ? other.getModelCode() == null : this.getModelCode().equals(other.getModelCode()))
            && (this.getModelName() == null ? other.getModelName() == null : this.getModelName().equals(other.getModelName()))
            && (this.getModelDesc() == null ? other.getModelDesc() == null : this.getModelDesc().equals(other.getModelDesc()))
            && (this.getModelGroup() == null ? other.getModelGroup() == null : this.getModelGroup().equals(other.getModelGroup()))
            && (this.getTokenNum() == null ? other.getTokenNum() == null : this.getTokenNum().equals(other.getTokenNum()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getCreateUser() == null ? other.getCreateUser() == null : this.getCreateUser().equals(other.getCreateUser()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getUpdateUser() == null ? other.getUpdateUser() == null : this.getUpdateUser().equals(other.getUpdateUser()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getModelCode() == null) ? 0 : getModelCode().hashCode());
        result = prime * result + ((getModelName() == null) ? 0 : getModelName().hashCode());
        result = prime * result + ((getModelDesc() == null) ? 0 : getModelDesc().hashCode());
        result = prime * result + ((getModelGroup() == null) ? 0 : getModelGroup().hashCode());
        result = prime * result + ((getTokenNum() == null) ? 0 : getTokenNum().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getCreateUser() == null) ? 0 : getCreateUser().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getUpdateUser() == null) ? 0 : getUpdateUser().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", modelCode=").append(modelCode);
        sb.append(", modelName=").append(modelName);
        sb.append(", modelDesc=").append(modelDesc);
        sb.append(", modelGroup=").append(modelGroup);
        sb.append(", tokenNum=").append(tokenNum);
        sb.append(", createTime=").append(createTime);
        sb.append(", createUser=").append(createUser);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", updateUser=").append(updateUser);
        sb.append(", isDelete=").append(isDelete);
        sb.append("]");
        return sb.toString();
    }
}