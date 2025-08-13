package com.wnhuang.common.websocket.config;


import com.wnhuang.common.websocket.handle.MyWebSocketHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

/**
 * 注册websocket处理器
 *
 * @author wnhuang
 * @created by Administrator on 2023-04-26-22:44
 */
@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketRegisterConfig implements WebSocketConfigurer {

    List<MyWebSocketHandle> webSocketHandlers;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        for (MyWebSocketHandle webSocketHandler : webSocketHandlers) {
            log.info("注册websocket处理器：{}", webSocketHandler.getPath());
            registry.addHandler(webSocketHandler, webSocketHandler.getPath()).setAllowedOrigins("*");
        }
    }

    @Autowired
    public void setWebSocketHandler(List<MyWebSocketHandle> webSocketHandlers) {
        this.webSocketHandlers = webSocketHandlers;
    }

}
