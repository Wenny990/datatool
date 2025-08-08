package com.wnhuang.common.utils;


import com.wnhuang.common.domain.entity.SystemUser;

/**
 * 通过拦截请求，存储当前上下文用户信息
 * @author wnhuang
 * @Package com.base.utils
 * @date 2022-07-25 21:04
 */
public class UserContentUtils {

    private static final ThreadLocal<SystemUser> threadLocal = new ThreadLocal<>();

    public static void addUser(SystemUser user) {
        threadLocal.set(user);
    }

    public static SystemUser getUser() {
        return threadLocal.get();
    }

    public static void removeUser() {
        threadLocal.remove();
    }
}
