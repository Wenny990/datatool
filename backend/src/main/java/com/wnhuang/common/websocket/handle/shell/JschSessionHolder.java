package com.wnhuang.common.websocket.handle.shell;

import cn.hutool.core.convert.Convert;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wnhuang
 * @Package com.wnhuang.common.websocket.handle.shell
 * @date 2025/8/11 17:18
 */
@Slf4j
public class JschSessionHolder implements SshSessionHolder {

    private final Session session;
    private final ChannelShell shellChannel;
    private final String characterSet;
    private CommandOutputCallback callback;
    private final ExecutorService executorService; // 用于管理后台线程

    public JschSessionHolder(Session session, ChannelShell shellChannel, String characterSet) {
        this.session = session;
        this.shellChannel = shellChannel;
        this.characterSet = characterSet;
        this.executorService = Executors.newSingleThreadExecutor(); // 初始化线程池
    }

    @Override
    public void write(String command) {
        try {
            OutputStream outputStream = shellChannel.getOutputStream();
            outputStream.write(command.getBytes(characterSet));
            outputStream.flush();
        } catch (IOException e) {
            log.error("写入命令失败: {}", e.getMessage());
            throw new RuntimeException("写入命令失败", e);
        }
    }

    @Override
    public void close() {
        // 关闭线程池
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }

        // 关闭shell channel
        if (shellChannel != null && shellChannel.isConnected()) {
            try {
                shellChannel.disconnect();
            } catch (Exception e) {
                log.warn("关闭shell channel时发生异常: {}", e.getMessage());
            }
        }
        log.info("关闭shell  channel成功");

//        // 关闭session
//        if (session != null && session.isConnected()) {
//            try {
//                session.disconnect();
//            } catch (Exception e) {
//                log.warn("关闭session时发生异常: {}", e.getMessage());
//            }
//        }
//        log.info("关闭ssh session成功");
    }

    public void resize(Map<String, Object> entries) {
        if (shellChannel != null && shellChannel.isConnected()) {
            int rows = Convert.toInt(entries.get("rows"), 50);
            int cols = Convert.toInt(entries.get("cols"), 100);
            int hp = Convert.toInt(entries.get("hp"), 480);
            int wp = Convert.toInt(entries.get("wp"), 980);
            shellChannel.setPtySize(cols, rows, wp, hp);
        }
    }

    public void init(CommandOutputCallback callback) {
        this.callback = callback;
        this.shellChannel.setPty(true);
        this.shellChannel.setPtyType("vt100");
        try {
            this.shellChannel.connect();
        } catch (JSchException e) {
            log.error("获取shellChannel连接异常: {}", e.getMessage());
            throw new RuntimeException("连接shell失败", e);
        }
        startReadOutput();
    }

    private void startReadOutput() {
        // 使用线程池来管理后台线程
        executorService.submit(() -> {
            byte[] buffer = new byte[32768];
            try (InputStream in = shellChannel.getInputStream()) {
                int exitStatus = shellChannel.getExitStatus();
                while (exitStatus == -1) {
                    // 检查线程是否被中断
                    if (Thread.currentThread().isInterrupted()) {
                        log.info("输出读取线程被中断");
                        break;
                    }

                    while (in.available() > 0) {
                        int i = in.read(buffer, 0, 32768);
                        if (i < 0) {
                            break;
                        }
                        String result = new String(buffer, 0, i, characterSet);
                        if (callback != null) {
                            callback.onOutput(result);
                        }
                    }

                    if (shellChannel.isClosed()) {
                        if (in.available() > 0) {
                            continue;
                        }
                        if (callback != null) {
                            callback.onComplete(shellChannel.getExitStatus());
                        }
                        break;
                    }

                    exitStatus = shellChannel.getExitStatus();

                    // 使用更合理的等待时间，并响应中断
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        log.info("输出读取线程被中断");
                        Thread.currentThread().interrupt(); // 恢复中断状态
                        break;
                    }
                }
            } catch (Exception e) {
                log.warn("获取ssh输出异常: {}", e.getMessage(), e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }
}
