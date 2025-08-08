package com.wnhuang.common.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.CharsetUtil;
import com.jcraft.jsch.*;
import com.wnhuang.common.domain.entity.MonitorServerInfo;
import com.wnhuang.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @author Administrator
 */
@Slf4j
public class JschUtil {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(8);

    private final static Map<String, Session> SESSION_CACHE = new ConcurrentHashMap<>();

    private final static JSch JSCH = new JSch();

    /**
     * 执行命令并返回结果
     *
     * @param command 待执行的命令
     * @return 命令的执行结果字符串
     */
    public static String execCommand(MonitorServerInfo serverInfo, String command) {
        Session session = null;
        ChannelExec channel = null;
        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();
        try {
            session = getSession(serverInfo);
            channel = (ChannelExec) session.openChannel("exec");
            log.info("服务器 {} 执行远程命令：{}", serverInfo.getServerName() ,command);
            channel.setCommand(command);
            InputStream in = channel.getInputStream();
            channel.connect();
            reader = new BufferedReader(new InputStreamReader(in));
            String buf;
            while ((buf = reader.readLine()) != null) {
                result.append(buf).append("\n");
            }
            log.info("服务器 {} 执行命令 返回结果：{}", serverInfo.getServerName(),  result.toString());
        } catch (JSchException | IOException e) {
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
                channel.disconnect();
            }
        }
        log.debug(result.toString());
        return result.toString();
    }

    /**
     * 异步执行远程命令
     *
     * @param serverInfo   服务器对象
     * @param command      执行的命令内容
     * @param logLineFunc  每一行日志的处理回调
     * @param callBackFunc 任务执行完成的回调
     * @return
     */
    public static void asyncExecCommand(MonitorServerInfo serverInfo, String command, Consumer<String> logLineFunc, Consumer<Integer> callBackFunc) {
        String taskId = UUID.randomUUID().toString();
        executorService.submit(() -> {
            Session session = null;
            ChannelExec channel = null;
            BufferedReader reader = null;
            ByteArrayOutputStream errStream = new ByteArrayOutputStream();
            try {
                session = getSession(serverInfo);
                channel = (ChannelExec) session.openChannel("exec");
                log.info("异步执行远程命令：{}", command);
                logLineFunc.accept("开始执行远程命令：" + command);
                channel.setCommand(command);
                InputStream in = channel.getInputStream();
                channel.setErrStream(errStream);
                channel.connect();
                reader = new BufferedReader(new InputStreamReader(in));
                String buf;
                while ((buf = reader.readLine()) != null) {
                    logLineFunc.accept(buf);
                }
                logLineFunc.accept("远程命令执行完成！执行结果：" + channel.getExitStatus());
                log.info("远程命令执行完成！{}", channel.getExitStatus());
            } catch (JSchException | IOException e) {
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
                    channel.disconnect();
                    if(channel.getExitStatus() != 0){
                        String errorMessage = "执行命令失败：" + errStream.toString();
                        log.error(errorMessage);
                        logLineFunc.accept(errorMessage);
                    }
                    callBackFunc.accept(channel.getExitStatus());
                }
            }
        });
    }

    public static String executeCommands(MonitorServerInfo serverInfo, List<String> commands) {
        Session session = null;
        Channel channel = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            session = getSession(serverInfo);
            channel = session.openChannel("shell");
            outputStream = channel.getOutputStream();
            inputStream = channel.getInputStream();
            channel.connect();
            for (String command : commands) {
                command = filterDangerousCommands(command);
                outputStream.write((command + "\n").getBytes());
                outputStream.flush();
            }

            byte[] buffer = new byte[1024];
            while (true) {
                while (inputStream.available() > 0) {
                    int i = inputStream.read(buffer, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    stringBuilder.append(new String(buffer, 0, i));
                }
                if (channel.isClosed()) {
                    stringBuilder.append("退出返回值: ").append(channel.getExitStatus());
                    break;
                }
                Thread.sleep(500);
            }
            return stringBuilder.toString();
        } catch (JSchException | IOException | InterruptedException e) {
            throw new BusinessException(e);
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            if (null != channel) {
                channel.disconnect();
            }
        }
    }

    /**
     * 向服务器写入一个文件
     *
     * @param serverInfo 服务器对象
     * @param path       路径
     * @param content    内容
     * @return 成功标识
     */
    public static Boolean uploadFile(MonitorServerInfo serverInfo, String path, String content) {
        InputStream is = new ByteArrayInputStream(content.getBytes());
        return uploadFile(serverInfo, path, is);
    }

    /**
     * 向服务器写入一个文件
     *
     * @param serverInfo 服务器对象
     * @param path       路径
     * @param is    输入文件内容
     * @return 成功标识
     */
    public static Boolean uploadFile(MonitorServerInfo serverInfo, String path, InputStream is) {
        Session session = null;
        ChannelSftp sftpChannel = null;
        try {
            session = getSession(serverInfo);
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            sftpChannel.put(is, path);
            return true;
        } catch (JSchException | SftpException e) {
            throw new BusinessException(e);
        } finally {
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
        }
    }

    /**
     * 判断目录是否存在，不存在就创建
     * @param serverInfo
     * @param path
     * @throws SftpException
     */
    public static void checkPathExist(MonitorServerInfo serverInfo, String path)  {
        // 检查并创建路径中的目录
        String directory = path.substring(0, path.lastIndexOf('/'));
        Session session = null;
        ChannelSftp sftpChannel = null;
        try {
            session = getSession(serverInfo);
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            sftpChannel.cd(directory);
        } catch (SftpException  e) {
            // 目录不存在，递归创建目录
            String[] folders = directory.split("/");
            String currentPath = "";
            for (String folder : folders) {
                if (folder.length() > 0) {  // 忽略空的路径部分
                    currentPath += "/" + folder;
                    try {
                        sftpChannel.cd(currentPath);
                    } catch (SftpException ex) {
                        try {
                            sftpChannel.mkdir(currentPath);
                            sftpChannel.cd(currentPath);
                        } catch (SftpException exc) {
                            throw new BusinessException(exc);
                        }
                    }
                }
            }
        } catch (JSchException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 检查服务器上是否存在指定文件
     *
     * @param serverInfo 服务器对象
     * @param filePath   文件路径
     * @return 如果文件存在返回true，否则返回false
     */
    public static Boolean isFileExists(MonitorServerInfo serverInfo, String filePath) {
        Session session = null;
        ChannelSftp sftpChannel = null;
        try {
            session = getSession(serverInfo);
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            // 检查文件是否存在
            sftpChannel.stat(filePath);
            return true;
        } catch (JSchException e) {
            throw new BusinessException(e);
        } catch (SftpException e) {
            // 如果是因为文件不存在引起的异常，则返回false
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            } else {
                throw new BusinessException(e);
            }
        } finally {
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
        }
    }


    /**
     * 获取服务器文件内容
     *
     * @param serverInfo 服务器对象
     * @param remoteFilePath 文件路径
     * @return 成功标识
     */
    public static String getRemoteFile(MonitorServerInfo serverInfo, String remoteFilePath) {
        Session session = null;
        ChannelSftp sftpChannel = null;
        InputStream inputStream = null;
        try {
            session = getSession(serverInfo);
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            inputStream = sftpChannel.get(remoteFilePath);
            return IoUtil.read(inputStream, CharsetUtil.charset(serverInfo.getCharacterSet()));
        } catch (JSchException | SftpException e) {
            throw new BusinessException("读取文件异常:" + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭文件流异常:{}", e.getMessage());
                }
            }
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
        }
    }

    public static Session getSession(MonitorServerInfo serverInfo) throws JSchException {
        return getSession(serverInfo.getServerIp(), Convert.toInt(serverInfo.getServerPort(), 22), serverInfo.getServerUser(), serverInfo.getServerPass(), serverInfo.getCharacterSet());
    }

    public static synchronized Session getSession(String host, Integer port, String userName, String password, String characterSet) {
        try {
            Session session = SESSION_CACHE.get(host);
            if (session == null || !session.isConnected()) {
                session = JSCH.getSession(userName, host, port);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setConfig("Charset", characterSet);
                session.setConfig("PreferredCharset", characterSet);
                session.setConfig("MaxSessions", String.valueOf(100));
                session.setConfig("MaxChannelsPerSession", String.valueOf(100));
                session.connect(30000);
                log.info("创建会话成功：{}", session.getHost());
                SESSION_CACHE.put(host, session);
            }
            return session;
        } catch (JSchException e) {
            log.error("连接服务器异常：{}", e.getMessage());
            throw new BusinessException("连接服务器失败，请检查后重试：" + e.getMessage());
        }
    }

    /**
     * 关闭所有会话
     */
    public static void closeSessions() {
        for (Session session : SESSION_CACHE.values()) {
            session.disconnect();
            log.info("关闭会话成功：" + session.getHost());
        }
    }

    public static void closeSession(String host){
        Session session = SESSION_CACHE.get(host);
        if(session != null){
            session.disconnect();
            log.info("关闭会话成功：" + host);
        }
    }

    public static String filterDangerousCommands(String input) {
        String[] dangerousCommands = {"rm", "shutdown", "reboot", "halt", "systemctl", "format", "mv", "cp", "tar"};
        for (String cmd : dangerousCommands) {
            if (input.contains(cmd)) {
                // 包含危险指令，进行过滤
                throw new BusinessException("危险指令");
            }
        }
        // 不包含危险指令，返回原字符串
        return input;
    }
}
