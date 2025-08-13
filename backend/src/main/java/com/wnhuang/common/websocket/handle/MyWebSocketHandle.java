package com.wnhuang.common.websocket.handle;

import org.springframework.web.socket.WebSocketHandler;

public interface MyWebSocketHandle extends WebSocketHandler {

    /**
     * 注册的路径
     * @return
     */
    String getPath();
}
