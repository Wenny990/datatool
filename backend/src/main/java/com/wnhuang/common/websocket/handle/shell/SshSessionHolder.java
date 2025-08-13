package com.wnhuang.common.websocket.handle.shell;

import java.util.Map;

/**
 * @author wnhuang
 * @Package com.wnhuang.common.websocket.handle.shell
 * @date 2025/8/11 17:00
 */
public interface SshSessionHolder {

    void init(CommandOutputCallback callback);

    void write(String command);

    void resize(Map<String, Object> config);

    void close();
}
