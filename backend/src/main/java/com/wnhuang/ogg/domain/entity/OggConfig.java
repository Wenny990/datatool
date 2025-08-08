package com.wnhuang.ogg.domain.entity;

import lombok.Data;

import java.util.List;

/**
 * @author wnhuang
 * @created by Administrator on 2023-04-23-22:07
 */
@Data
public class OggConfig {
    private Integer sourceServerId;
    private Integer targetServerId;

    private Integer sourceOrTarget;
    /**
     * 抽取进程参数
     */
    private String extParams;

    /**
     * 投递进程参数
     */
    private String pumParams;

    /**
     * 复制进程参数
     */
    private String repParams;

    /**
     * 源端命令
     */
    private String sourceCommand;

    /**
     * 目标端命令
     */
    private String targetCommand;

    /**
     * 源端初始化参数
     */
    private String einParams;

    /**
     * 目标端初始化参数,可以有多个
     */
    private List<String> rinParams;
}
