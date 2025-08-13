package com.wnhuang.common.websocket.handle.shell;

/**
 * @author wnhuang
 * @Package com.wnhuang.common.websocket.handle.shell
 * @date 2025/8/11 17:13
 */
public interface CommandOutputCallback {

    void onOutput(String output);

    void onError(String error);

    void onComplete(int exitCode);
}
