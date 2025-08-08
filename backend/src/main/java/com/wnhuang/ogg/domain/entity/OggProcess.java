package com.wnhuang.ogg.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author wnhuang
 * @created by Administrator on 2023-04-17-22:12
 */
@Data
public class OggProcess {

    /**
     * 进程名称
     */
    private String processName;

    /**
     * 服务器id
     */
    private Integer serverId;

    /**
     * 所在服务器
     */
    private String serverName;

    /**
     * 进程类型
     */
    private Integer type;

    /**
     * 状态
     */
    private Integer status;

    private LocalDateTime LastStartDate;

    private String lagAtChkpt;

    private String timeSinceChkpt;

    private List<OggProcessDetail> details;

    /**
     * 进程参数
     */
    private String params;

    /**
     * 延迟
     */
    private String checkPointLag;

    private LocalTime updatedAgo;

    private Integer processId;

    /**
     * 检查点文件
     */
    private String checkPointFile;

    private Integer checkPointRba;

    private LocalDateTime checkPointTime;

    /**
     * 日志内容
     */
    private String report;

    /**
     * 定义文件参数
     */
    private String defParams;

}
