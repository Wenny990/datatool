package com.wnhuang.common.websocket.handle.shell;

/**
 * @author wnhuang
 * @created by Administrator on 2023-04-26-1:46
 */

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wnhuang.common.domain.entity.MonitorServerInfo;
import com.wnhuang.common.service.CommandExecuteService;
import com.wnhuang.common.service.MonitorServerInfoService;
import com.wnhuang.common.websocket.handle.MyWebSocketHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketShellTool extends TextWebSocketHandler implements MyWebSocketHandle {

    @Autowired
    MonitorServerInfoService monitorServerInfoService;

    @Autowired
    CommandExecuteService commandExecuteService;

    private final ConcurrentHashMap<String, SshSessionHolder> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket连接已打开，会话ID： {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        JSONObject entries;
        String payload = message.getPayload();
        if (!JSONUtil.isTypeJSONObject(payload)) {
            entries = new JSONObject().putOnce("type", "exec").putOnce("command", payload);
        } else {
            entries = JSONUtil.parseObj(payload);
        }
        String type = (String) entries.get("type");
        SshSessionHolder shellCommandHandler = null;
        if ("init".equals(type)) {
            try {
                init(session, entries);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            shellCommandHandler = sessions.get(session.getId());
            Assert.notNull(shellCommandHandler, "尚未初始化，或会话已关闭");
        }
        if ("exec".equals(type)) {
            shellCommandHandler.write(payload);
        }
        if ("resize".equals(type)) {
            shellCommandHandler.resize(entries);
        }
    }

    private void init(WebSocketSession wsSession, JSONObject entries) throws IOException {
        Integer key = Convert.toInt(entries.get("serverId"));
        MonitorServerInfo serverInfo = monitorServerInfoService.getById(key);
        wsSession.sendMessage(new TextMessage("正在创建会话...\r\n"));
        SshSessionHolder shellSession = commandExecuteService.createShellSession(serverInfo);
        shellSession.init(new CommandOutputCallback() {
            @Override
            public void onOutput(String output) {
                try {
                    wsSession.sendMessage(new TextMessage(output));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(String error) {
                try {
                    wsSession.sendMessage(new TextMessage("服务器出错，" + error));
                    shellSession.close();
                    wsSession.close();
                    sessions.remove(wsSession.getId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onComplete(int exitCode) {
                try {
                    if(wsSession.isOpen()) wsSession.sendMessage(new TextMessage("连接已退出，退出状态：" + exitCode + "\r\n"));
                    shellSession.close();
                    wsSession.close();
                    sessions.remove(wsSession.getId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        try {
            wsSession.sendMessage(new TextMessage(serverInfo.getServerIp() + "服务器初始化成功！\r\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sessions.put(wsSession.getId(), shellSession);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket 发生错误: {}", exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        SshSessionHolder shellCommandHandler = sessions.get(sessionId);
        if (shellCommandHandler != null) {
            shellCommandHandler.close();
        }
        sessions.remove(sessionId);
        log.info("WebSocket连接已关闭，会话ID：{}", sessionId);
    }

    /**
     * 注册的路径
     *
     * @return
     */
    @Override
    public String getPath() {
        return "/shell";
    }

}