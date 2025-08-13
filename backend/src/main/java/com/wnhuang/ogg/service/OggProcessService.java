package com.wnhuang.ogg.service;

import com.wnhuang.ogg.domain.entity.OggConfig;
import com.wnhuang.ogg.domain.entity.OggGenerateConfig;
import com.wnhuang.ogg.domain.entity.OggProcess;
import com.wnhuang.ogg.domain.request.*;
import com.wnhuang.ogg.domain.response.OggDefFileResp;
import com.wnhuang.ogg.domain.response.OggMonitorVo;

import java.util.List;

/**
* @author Administrator
* @description 针对表【monitor_server_info(服务器监控表)】的数据库操作Service
* @createDate 2023-04-17 21:45:39
*/
public interface OggProcessService  {

    /**
     * 获取服务器上的进程列表信息
     * @param serverId 服务器逐渐
     * @return ogg进程信息
     */
    List<OggProcess> getOggProcess(Integer serverId);

    /**
     * 获取进程的参数信息
     * @param serverId 服务器id
     * @param processName 进程名称
     * @return 参数详情
     */
    OggProcess getProcessParams(Integer serverId,String processName);

    /**
     * 获取进程的详情
     * @param serverId 服务器id
     * @param processName 进程名称
     * @return 参数详情
     */
    OggProcess getProcessDetail(Integer serverId,String processName);

    /**
     * 启动或关闭进程
     * @param serverId 服务器id
     * @param processName 进程名称
     * @param isStart 是启动
     * @return 参数详情
     */
    Boolean startOrStopProcess(Integer serverId,String processName,Boolean isStart);

    /**
     * 获取进程信息
     * @param serverId 服务器id
     * @param processName 进程名称
     * @return 进程信息
     */
    OggProcess getProcessInfo(Integer serverId,String processName);

    /**
     * 获取所有进程信息
     * @return
     */
    List<OggMonitorVo> getOggMonitorInfo();

    /**
     * 获取指定服务器上的进程状态信息
     * @param serverIdList 服务器id
     * @return
     */
    List<OggMonitorVo> getOggMonitorInfo(List<Integer> serverIdList);


    /**
     * 保存进程参数文件
     * @param oggProcess 进程信息
     * @return 成功标识
     */
    Boolean editProcessParams(OggProcess oggProcess);



    /**
     * 查看进程的日志
     * @param serverId 服务器id
     * @param processName 进程名称
     * @return 参数详情
     */
    OggProcess getProcessReport(Integer serverId,String processName);

    /**
     * 根据配置类，生成进程参数
     * @param oggGenerateConfig 生成配置
     * @return 生成的参数
     */
    OggConfig generateOggConfig(OggGenerateConfig oggGenerateConfig);

    /**
     * 使用shell执行多个命令
     *
     * @param oggConfig 生成配置
     * @return 执行结果
     */
    String executeMultipleCommands(OggConfig oggConfig);

    /**
     * 删除进程
     * @param serverId 服务器id
     * @param processName 进程名称
     * @return
     */
    String deleteProcess(Integer serverId,String processName);

    /**
     * 获取进程的定义文件参数信息
     *
     * @param oggDefFileRequest
     * @return 定义文件参数详情
     */
    OggDefFileResp getProcessDefParams(OggDefFileBaseRequest oggDefFileRequest);

    /**
     * 保存定义文件参数信息
     * @param oggDefFileSaveRequest
     * @return
     */
    Boolean saveProcessDefParams(OggDefFileSaveRequest oggDefFileSaveRequest);

    /**
     * 生成进程的定义文件
     *
     * @param oggDefFileRequest 服务器id
     * @return 生成结果
     */
    OggDefFileResp defgenDefsFile(OggDefFileBaseRequest oggDefFileRequest);

    OggDefFileResp getDefsFileContent(OggDefFileBaseRequest oggDefFileRequest);

    /**
     * 上传定义文件到目标端
     *
     * @param oggDefFileUploadRequest 请求体
     * @return
     */
    Boolean uploadDefsFile(OggDefFileUploadRequest oggDefFileUploadRequest);

    /**
     * 预览命令
     *
     * @param request 请求体
     * @return
     */
    String commandPreview(OggCommandRequest request);

    /**
     * ogg控制台下执行命令
     * @param request
     * @return
     */
    String commandByOgg(OggCommandListRequest request);
}
