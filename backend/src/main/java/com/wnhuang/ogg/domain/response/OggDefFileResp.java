package com.wnhuang.ogg.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author wnhuang
 * @date 2024/10/14 21:00
 */
@Data
@Builder
@AllArgsConstructor
public class OggDefFileResp {

    /**
     * 抽取进程名称
     */
    private String processName;

    /**
     * 源库
     */
    private Integer serverId;

    /**
     * 定义文件生成参数名称
     */
    private String defParamName;

    /**
     * 定义文件生成参数内容
     */
    private String defParamContent;

    /**
     * 定义文件名称
     */
    private String defFileName;

    /**
     * 定义文件内容
     */
    private String fileContent;

    /**
     * 生成结果
     */
    private String defgenResult;
}
