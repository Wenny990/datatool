package com.wnhuang.common.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.*;
import com.wnhuang.common.domain.entity.MonitorServerInfo;
import com.wnhuang.common.exception.BusinessException;
import com.wnhuang.common.service.CommandExecuteService;
import com.wnhuang.common.service.MonitorServerInfoService;
import com.wnhuang.common.service.SessionPool;
import com.wnhuang.common.utils.GroupLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.io.*;
import java.util.function.Consumer;

/**
 * 远程执行命令
 *
 * @author wnhuang
 * @date 2024/11/1 00:29
 */
@Slf4j
public class SshCommandExecuteServiceImpl implements CommandExecuteService {


    @Value("${SshCommand.dangerousCommands:''}")
    private String DANGEROUS_COMMANDS;

    private final SessionPool sessionPool;



    @Autowired
    public SshCommandExecuteServiceImpl(SessionPool sessionPool) {
        this.sessionPool = sessionPool;
    }


    @Override
    public void executeCommand(MonitorServerInfo serverInfo, String command, Consumer<String> logLineFunc, Consumer<Integer> callBackFunc) {
        Session session = null;
        ChannelExec channel = null;
        BufferedReader reader = null;
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        String characterSet = serverInfo.getCharacterSet();
        if (StrUtil.isBlank(characterSet)){
            characterSet = "UTF-8";
        }
        try {
            log.info("开始执行命令 {}", command);
            session = getSession(serverInfo);
            channel = sessionPool.createExecChannel(session, command);
            logLineFunc.accept("开始执行命令：" + command);
            InputStream in = channel.getInputStream();
            channel.setErrStream(errStream);
//            channel.connect(3000);
            reader = new BufferedReader(new InputStreamReader(in, characterSet));
            String buf;
            while ((buf = reader.readLine()) != null) {
                logLineFunc.accept(buf);
            }
            logLineFunc.accept("命令执行完成，返回结果：" + channel.getExitStatus());
            log.info("命令执行完成，返回结果：{}", channel.getExitStatus());
        } catch (JSchException | IOException e) {
            log.error("远程执行命令异常：{}", e.getMessage());
            throw new BusinessException(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            if (channel != null) {
                sessionPool.releaseChannel(session, channel);
                if (channel.getExitStatus() != 0) {
                    String errorMessage = null;
                    try {
                        errorMessage = "退出状态失败，信息：" + errStream.toString(characterSet);
                    } catch (UnsupportedEncodingException e) {
                        log.warn("编码格式异常", e);
                        errorMessage = "";
                    }
                    log.error(errorMessage);
                    logLineFunc.accept(errorMessage);
                }
                callBackFunc.accept(channel.getExitStatus());
            }
        }

    }

    /**
     * 指定服务器上执行命令
     *
     * @param serverInfo 服务器信息
     * @param command    命令内容
     * @return 执行结果
     */
    @Override
    public String executeCommand(MonitorServerInfo serverInfo, String command) {
        Session session = null;
        ChannelExec channel = null;
        try {
            filterDangerousCommands(command);
            log.info("服务器 {} 执行命令: {}", serverInfo.getServerName(), command);
            // 获取会话
            session = getSession(serverInfo);
            // 创建命令执行通道
            channel = sessionPool.createExecChannel(session, command);

            // 读取命令输出
            InputStream in = channel.getInputStream();
            byte[] buffer = new byte[1024];
            StringBuilder output = new StringBuilder();
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                output.append(new String(buffer, 0, bytesRead, serverInfo.getCharacterSet()));
            }
            log.info("服务器 {} 执行命令 {} 结果: {}", serverInfo.getServerName(), command, output.substring(0, output.length() > 2000 ? 2000 : output.length()));
            return output.toString();
        } catch (Exception e) {
            log.error("远程执行命令异常，", e.getMessage());
            throw new BusinessException("远程执行命令异常，" + e.getMessage());
        } finally {
            // 释放通道
            if (channel != null) {
                sessionPool.releaseChannel(session, channel);
            }
        }
    }



    private Session getSession(MonitorServerInfo serverInfo) throws JSchException {
        return sessionPool.getSession(serverInfo.getServerIp(), Convert.toInt(serverInfo.getServerPort(), 22), serverInfo.getServerUser(), serverInfo.getServerPass());
    }




    @Override
    public Boolean uploadFile(MonitorServerInfo serverInfo, String path, String content) {
        return uploadFile(serverInfo, path, new ByteArrayInputStream(content.getBytes()));
    }

    @Override
    public Boolean uploadFile(MonitorServerInfo serverInfo, String path, InputStream is) {
        Session session = null;
        ChannelSftp sftpChannel = null;
        try {
            session = getSession(serverInfo);
            sftpChannel = sessionPool.createSftpChannel(session);
            sftpChannel.put(is, path);
            return true;
        } catch (JSchException | SftpException e) {
            log.error("上传文件到服务器异常，{}", e.getMessage());
            throw new BusinessException("上传文件到服务器异常，" + e.getMessage());
        } finally {
            sessionPool.releaseChannel(session, sftpChannel);
        }
    }


    /**
     * 创建目录如果不存在
     *
     * @param serverInfo 服务器信息
     * @param path       路径
     * @return 创建结果
     */
    @Override
    public Boolean createDirIfNotExist(MonitorServerInfo serverInfo, String path) {
        String directory = path.substring(0, path.lastIndexOf('/'));
        Session session = null;
        ChannelSftp sftpChannel = null;
        try {
            session = getSession(serverInfo);
            sftpChannel = sessionPool.createSftpChannel(session);
            sftpChannel.cd(directory);
        } catch (SftpException e) {
            // 目录不存在则创建
            String[] folders = directory.split("/");
            String currentPath = "";
            for (String folder : folders) {
                if (folder.length() > 0) {  //
                    currentPath += "/" + folder;
                    try {
                        sftpChannel.cd(currentPath);
                    } catch (SftpException ex) {
                        try {
                            sftpChannel.mkdir(currentPath);
                            sftpChannel.cd(currentPath);
                        } catch (SftpException exc) {
                            throw new BusinessException(exc.getMessage());
                        }
                    }
                }
            }
        } catch (JSchException e) {
            throw new BusinessException(e.getMessage());
        }
        return true;
    }


    /**
     * 过滤危险命令
     *
     * @param input
     */
    private void filterDangerousCommands(String input) {
        String[] dangerousCommands = DANGEROUS_COMMANDS.split(",");
        for (String cmd : dangerousCommands) {
            if (input.contains(cmd)) {
                // 包含危险指令，进行过滤
                throw new BusinessException("包含危险指令：" + cmd);
            }
        }
    }
}
