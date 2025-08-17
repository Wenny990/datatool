package com.wnhuang.datax.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author wnhuang
 * @date 2024/10/23 00:14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataxJobRunLog {


    private String uuid;
    private Integer jobId;
    private String jobName;
    private String jobPath;
    private String shellCommand;
    private String serverName;
    private String serverIp;
    private String logPath;
    private Integer serverId;
    private Date startTime;
    private String currWhereStr;

    /**
     * 任务状态，-1: 未开始,0: 执行中 1: 执行成功 2: 执行失败
     */
    private Integer status;

    /**
     * 当前任务包含的全部数量
     */
    private Integer total ;
    /**
     * 当前是第几个任务
     */
    private Integer current;

    private Long speed;

    /**
     * 百分比
     */
    private Double percentage;
    /**
     * 总记录数
     */
    private Long totalRecords;
    /**
     * 执行结果
     */
    private Integer result;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 是否有后续任务
     */
    private Boolean hasNext;
    /**
     * 后续任务的id
     */
    private String nextUuid;

    /**
     * 任务日志内容
     */
    private String logContent;
}
