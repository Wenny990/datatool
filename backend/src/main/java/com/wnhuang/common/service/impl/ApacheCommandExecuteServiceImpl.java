package com.wnhuang.common.service.impl;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.wnhuang.common.domain.entity.MonitorServerInfo;
import com.wnhuang.common.exception.BusinessException;
import com.wnhuang.common.service.CommandExecuteService;
import com.wnhuang.common.websocket.handle.shell.ApacheSshdSessionHolder;
import com.wnhuang.common.websocket.handle.shell.SshSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;

import java.io.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author wnhuang
 * @date 2025/1/13 19:31
 */
@Slf4j
public class ApacheCommandExecuteServiceImpl implements CommandExecuteService {

    private SshSessionManager sessionManager;

    public ApacheCommandExecuteServiceImpl() {
        sessionManager = new SshSessionManager();
    }


    @Override
    public void executeCommand(MonitorServerInfo serverInfo, String command, Consumer<String> logLineFunc, Consumer<Integer> callBackFunc) {
        ClientSession session = sessionManager.getSession(serverInfo);
        try (ChannelExec channel = session.createExecChannel(command);) {
            log.info("服务器 {} 开始实时执行命令: {}", sessionManager.generateKey(serverInfo), command);
            // 使用 PipedStream 实现实时读取
            PipedOutputStream pipedOut = new PipedOutputStream();
            PipedInputStream pipedIn = new PipedInputStream(pipedOut);
            BufferedReader reader = new BufferedReader(new InputStreamReader(pipedIn, serverInfo.getCharacterSet()));
            // 设置通道输出到 PipedOutputStream
            channel.setOut(pipedOut);
            channel.setErr(pipedOut);
            try {
                // 打开通道
                channel.open().verify(5, TimeUnit.SECONDS);
                // 启动一个线程实时读取输出
                new Thread(() -> {
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            logLineFunc.accept(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0);
                callBackFunc.accept(channel.getExitStatus());
            } catch (Exception e) {
                log.error("Failed to execute command: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String executeCommand(MonitorServerInfo serverInfo, String command) {
        ClientSession session = sessionManager.getSession(serverInfo);
        if (session == null) {
            throw new RuntimeException("Failed to establish SSH session.");
        }
        String result = null;
        try (ChannelExec channel = session.createExecChannel(command);) {
            log.info("服务器 {} 开始执行命令: {}", sessionManager.generateKey(serverInfo), command);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ByteArrayOutputStream outputErr = new ByteArrayOutputStream();
            channel.setOut(output);
            channel.setErr(outputErr);
            channel.open();
            channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0);
            result = output.toString(serverInfo.getCharacterSet());
            log.info("服务器 {} 执行命令: {} 结果: {} 返回内容: {}", sessionManager.generateKey(serverInfo), command, channel.getExitStatus(), result.substring(0, result.length() > 2000 ? 2000 : result.length()));
            String errString = outputErr.toString(serverInfo.getCharacterSet());
            if (StrUtil.isNotBlank(errString)) {
                log.error("Error：{}", errString);
            }
        } catch (Exception e) {
            log.error("命令执行失败", e);
            throw new BusinessException("命令执行失败." + e.getMessage());
        }
        return result;
    }


//    @Override
//    public String executeCommand(Integer serverId, String command) {
//        // 假设你有一个方法可以根据 serverId 获取 MonitorServerInfo，这里仅为示例
//        MonitorServerInfo serverInfo = monitorServerInfoService.getById(serverId);
//        return executeCommand(serverInfo, command);
//    }


//    @Override
//    public Boolean uploadFile(Integer serverId, String path, String content) {
//        MonitorServerInfo serverInfo = monitorServerInfoService.getById(serverId);
//        return uploadFile(serverInfo, path, content);
//    }

    @Override
    public Boolean uploadFile(MonitorServerInfo serverInfo, String path, String content) {
        return uploadFile(serverInfo, path, new ByteArrayInputStream(content.getBytes()));
    }

    @Override
    public Boolean uploadFile(MonitorServerInfo serverInfo, String path, InputStream is) {
        ClientSession session = sessionManager.getSession(serverInfo);
        try (SftpClient sftp = SftpClientFactory.instance().createSftpClient(session)) {
//                sftp.mkdir(path);
            SftpClient.CloseableHandle handle = sftp.open(path, SftpClient.OpenMode.Write, SftpClient.OpenMode.Create, SftpClient.OpenMode.Truncate);
            int buff_size = 1024 * 1024;
            byte[] src = new byte[buff_size];
            int len;
            long fileOffset = 0L;
            while ((len = is.read(src)) != -1) {
                sftp.write(handle, fileOffset, src, 0, len);
                fileOffset += len;
            }
            log.info("服务器 {} 文件上传成功，{} ", sessionManager.generateKey(serverInfo), path);
        } catch (Exception e) {
            log.error("服务器 {} 文件上传失败，{} ", sessionManager.generateKey(serverInfo), path, e);
            throw new RuntimeException(e);
        }
        return false;
    }

//    @Override
//    public Boolean uploadFile(Integer serverId, String path, InputStream is) {
//        MonitorServerInfo serverInfo = monitorServerInfoService.getById(serverId);
//        return uploadFile(serverInfo, path, is);
//    }

    @Override
    public Boolean createDirIfNotExist(MonitorServerInfo serverInfo, String path) {
        executeCommand(serverInfo, "mkdir -p " + path);
        return true;
    }

    @Override
    public SshSessionHolder createShellSession(MonitorServerInfo serverInfo) {
        try {
            ClientSession session = sessionManager.getSession(serverInfo);
            ChannelShell shellChannel = session.createShellChannel();
            return new ApacheSshdSessionHolder(session, shellChannel, serverInfo.getCharacterSet());
        } catch (IOException e) {
            log.error("创建shell会话异常，{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    class SshSessionManager {
        private SshClient client;
        private Map<String, ClientSession> sessionMap; // 使用 Map 存储不同服务器的会话

        public SshSessionManager() {
            client = SshClient.setUpDefaultClient();
            client.start();
            sessionMap = new HashMap<>();
        }

        public ClientSession getSession(MonitorServerInfo serverInfo) {
            String key = generateKey(serverInfo); // 生成服务器信息的唯一键
            ClientSession session = sessionMap.get(key);
            if (session == null || !session.isOpen()) {
                try {
                    session = client.connect(serverInfo.getServerUser(), serverInfo.getServerIp(), Convert.toInt(serverInfo.getServerPort(), 22))
                            .verify(15, TimeUnit.SECONDS).getSession();
                    session.addPasswordIdentity(serverInfo.getServerPass());
                    session.auth().verify(15, TimeUnit.SECONDS);
                    log.info("服务器会话创建成功 {}:{}", serverInfo.getServerName(), key);
                    sessionMap.put(key, session); // 将新创建的会话存储到 Map 中
                } catch (Exception e) {
                    log.error("服务器会话 {} 创建失败！！！", key, e);
                    throw new BusinessException("连接服务器失败，" + e.getMessage());
                }
            }
            return session;
        }

        public void closeSession() {
            for (ClientSession session : sessionMap.values()) {
                if (session != null && session.isOpen()) {
                    try {
                        session.close();
                    } catch (IOException e) {
                        log.error("关闭服务器会话失败", e);
                    }
                }
            }
            sessionMap.clear(); // 清理 Map
            if (client != null && client.isStarted()) {
                client.stop();
            }
        }

        public String generateKey(MonitorServerInfo serverInfo) {
            // 生成服务器信息的唯一键，确保可以唯一标识一个服务器
            return "[" + serverInfo.getServerName() + "]" + serverInfo.getServerUser() + "@" + serverInfo.getServerIp() + ":" + serverInfo.getServerPort();
        }
    }
}

