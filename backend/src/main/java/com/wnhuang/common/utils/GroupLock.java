package com.wnhuang.common.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wnhuang
 * @date 2024/11/20 23:41
 */
public class GroupLock {
    // 存储每个 key 的锁
    private final ConcurrentHashMap<String, Lock> lockMap = new ConcurrentHashMap<>();

    /**
     * 获取某个 key 的锁
     */
    public Lock getLock(String key) {
        // 使用 computeIfAbsent 确保锁的创建是线程安全的
        return lockMap.computeIfAbsent(key, k -> new ReentrantLock());
    }

    /**
     * 释放 key 的锁
     */
    public void releaseLock(String key) {
        lockMap.remove(key);
    }
}
