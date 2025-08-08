package com.wnhuang.common.service;

import com.wnhuang.common.domain.entity.MonitorServerInfo;

import java.io.InputStream;
import java.util.function.Consumer;

/**
 * 命令执行服务
 *
 * @author wnhuang
 * @date 2024/11/1 00:26
 */
public interface CommandExecuteService {

    /**
     * 执行命令，实时返回输出内容
     *
     * @param serverInfo   服务器
     * @param command      命令内容
     * @param logLineFunc  消息回调函数
     * @param callBackFunc 执行完成回调函数
     * @return 执行结果
     */
    void executeCommand(MonitorServerInfo serverInfo, String command, Consumer<String> logLineFunc, Consumer<Integer> callBackFunc);

    /**
     * 指定服务器上执行命令
     *
     * @param serverInfo 服务器信息
     * @param command    命令内容
     * @return 执行结果
     */
    String executeCommand(MonitorServerInfo serverInfo, String command);





    Boolean uploadFile(MonitorServerInfo serverInfo, String path, String content);

    Boolean uploadFile(MonitorServerInfo serverInfo, String path, InputStream is);


    /**
     * 创建目录，如果不存在
     *
     * @param serverInfo 服务器信息
     * @param path       目录路径
     * @return 是否成功
     */
    Boolean createDirIfNotExist(MonitorServerInfo serverInfo, String path);
}
