package com.wnhuang.api.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API执行日志实体类
 * @author wnhuang
 * @date 2024/12/01
 */
@Data
@TableName("t_api_execute_log")
public class ApiExecuteLog {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * API配置ID
     */
    private Integer apiConfigId;

    /**
     * API编码
     */
    private String apiCode;

    /**
     * 请求参数（JSON格式）
     */
    private String requestParams;

    /**
     * 响应结果（JSON格式）
     */
    private String responseResult;

    /**
     * 执行状态：1-成功，0-失败
     */
    private Integer executeStatus;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行耗时（毫秒）
     */
    private Long executionTime;

    /**
     * 执行时间
     */
    private LocalDateTime executeTime;

    /**
     * 请求IP
     */
    private String requestIp;
}