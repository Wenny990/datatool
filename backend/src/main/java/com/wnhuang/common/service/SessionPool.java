package com.wnhuang.common.service;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话池
 *
 * @author wnhuang
 */
@Component
@Slf4j
public class SessionPool {

    private static final int SESSION_TIMEOUT = 1 * 60 * 1000; // 会话空闲超时时间，5分钟

    // 用于存储和管理会话
    private final ConcurrentHashMap<String, SessionWrapper> sessionPool = new ConcurrentHashMap<>();
    private final Timer sessionCleanupTimer = new Timer(true); // 定时任务清理器

    public SessionPool() {
        // 定期清理空闲会话
        sessionCleanupTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                cleanUpIdleSessions();
            }
        }, SESSION_TIMEOUT, SESSION_TIMEOUT);
    }


    @PreDestroy
    //关闭所有会话
    public void closeAllSessions() {
        for (SessionWrapper sessionWrapper : sessionPool.values()) {
            sessionWrapper.session.disconnect();
        }
        sessionPool.clear();
    }

    // 获取或创建新的会话
    public Session getSession(String host, int port, String username, String password) throws JSchException {
        SessionWrapper sessionWrapper = sessionPool.get(host);
        if (null != sessionWrapper) {
            if (sessionWrapper.isConnected()) {
                sessionWrapper.updateLastUsed(); // 更新最后使用时间
                return sessionWrapper.session;
            } else {
                sessionPool.remove(host);
            }
        }

        log.info("没有空闲会话，创建新会话，{}", host);
        // 没有空闲会话或会话已达到通道上限，创建新会话
        Session session = new JSch().getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(30000); // 连接超时时间
        SessionWrapper newSessionWrapper = new SessionWrapper(session);
        sessionPool.put(host, newSessionWrapper);
        return newSessionWrapper.session;
    }

    // 创建命令执行通道
    public ChannelExec createExecChannel(Session session, String command) throws JSchException {
        SessionWrapper sessionWrapper = findSessionWrapper(session);
        if (sessionWrapper == null || !sessionWrapper.isConnected()) {
            throw new IllegalStateException("会话不可用或未找到！");
        }

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.connect(15000);
        return channel;
    }

    public ChannelSftp createSftpChannel(Session session) throws JSchException {
        SessionWrapper sessionWrapper = findSessionWrapper(session);
        if (sessionWrapper == null || !sessionWrapper.isConnected()) {
            throw new IllegalStateException("会话不可用或未找到！");
        }
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect(15000);
        return channel;
    }

    // 释放通道
    public void releaseChannel(Session session, Channel channel) {
        channel.disconnect();
        SessionWrapper sessionWrapper = findSessionWrapper(session);
        if (sessionWrapper != null) {
            sessionWrapper.updateLastUsed();
        }
    }

    // 定期清理空闲会话
    private void cleanUpIdleSessions() {
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<String, SessionWrapper> entry : sessionPool.entrySet()) {
            SessionWrapper sessionWrapper = entry.getValue();
            if (sessionWrapper.isIdle(currentTime)) {
                sessionWrapper.disconnect();
                sessionPool.remove(entry.getKey());
                log.info("空闲会话已关闭：{}", entry.getKey());
            }
        }
    }

    // 查找会话包装器
    private SessionWrapper findSessionWrapper(Session session) {
        for (SessionWrapper wrapper : sessionPool.values()) {
            if (wrapper.session.equals(session)) {
                return wrapper;
            }
        }
        return null;
    }

    // 会话包装类，管理每个会话的通道和空闲状态
    private static class SessionWrapper {
        private final Session session;

        private volatile long lastUsed;

        public SessionWrapper(Session session) {
            this.session = session;
            updateLastUsed();
        }


        // 更新会话的最后使用时间
        public void updateLastUsed() {
            this.lastUsed = System.currentTimeMillis();
        }

        // 判断会话是否空闲超时
        public boolean isIdle(long currentTime) {
            return (currentTime - lastUsed > SESSION_TIMEOUT);
        }

        public boolean isConnected() {
            return session.isConnected();
        }

        public void disconnect() {
            session.disconnect();
        }
    }
}
