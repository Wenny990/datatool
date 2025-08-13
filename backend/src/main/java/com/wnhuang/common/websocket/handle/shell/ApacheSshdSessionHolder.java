package com.wnhuang.common.websocket.handle.shell;

import cn.hutool.core.convert.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.session.ClientSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Apache SSHD 实现的会话持有者
 *
 * @author wnhuang
 * @Package com.wnhuang.common.websocket.handle.shell
 * @date 2025/8/11 17:18
 */
@Slf4j
public class ApacheSshdSessionHolder implements SshSessionHolder {

    private final ClientSession session;
    private final ChannelShell shellChannel;
    private final String characterSet;
    private CommandOutputCallback callback;
    private final ExecutorService executorService; // 用于管理后台线程

    public ApacheSshdSessionHolder(ClientSession session, ChannelShell shellChannel, String characterSet) {
        this.session = session;
        this.shellChannel = shellChannel;
        this.characterSet = characterSet;
        this.executorService = Executors.newSingleThreadExecutor(); // 初始化线程池
    }

    @Override
    public void write(String command) {
        try {
            shellChannel.getInvertedIn().write(command.getBytes(characterSet));
            shellChannel.getInvertedIn().flush();
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
        if (shellChannel != null && shellChannel.isOpen()) {
            try {
                shellChannel.close();
            } catch (Exception e) {
                log.warn("关闭shell channel时发生异常: {}", e.getMessage());
            }
        }
        log.info("关闭shell channel成功");

//        // 关闭session
//        if (session != null && session.isOpen()) {
//            try {
//                session.close();
//            } catch (Exception e) {
//                log.warn("关闭session时发生异常: {}", e.getMessage());
//            }
//        }
//        log.info("关闭ssh session成功");
    }

    public void resize(Map<String, Object> entries) {
        if (shellChannel != null && shellChannel.isOpen()) {
            // Apache SSHD 的调整大小方法可能与 JSch 不同
            // 这里根据实际需要进行调整
            int rows = Convert.toInt(entries.get("rows"), 50);
            int cols = Convert.toInt(entries.get("cols"), 100);
            // Apache SSHD 可能没有直接的 setPtySize 方法，需要查阅具体API
            // 如果有相应方法可以在这里实现
            log.debug("调整终端大小: rows={}, cols={}", rows, cols);
        }
    }

    public void init(CommandOutputCallback callback) {
        this.callback = callback;
        try {
            // Apache SSHD 可能不需要像 JSch 那样显式设置 pty 参数
            shellChannel.open();
        } catch (Exception e) {
            log.error("获取shellChannel连接异常: {}", e.getMessage());
            throw new RuntimeException("连接shell失败", e);
        }
        startReadOutput();
    }

    private void startReadOutput() {
        // 使用线程池来管理后台线程
        executorService.submit(() -> {
            byte[] buffer = new byte[32768];
            try (InputStream in = shellChannel.getInvertedOut()) {
                while (shellChannel.isOpen()) {
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

                    // 使用更合理的等待时间，并响应中断
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        log.info("输出读取线程被中断");
                        Thread.currentThread().interrupt(); // 恢复中断状态
                        break;
                    }
                }

                // 会话结束后调用 onComplete
                if (callback != null) {
                    callback.onComplete(shellChannel.getExitStatus());
                }
            } catch (Exception e) {
                log.error("获取ssh输出异常: {}", e.getMessage(), e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }
}
