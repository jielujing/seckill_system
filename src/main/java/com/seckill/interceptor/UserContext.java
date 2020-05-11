package com.seckill.interceptor;

import com.seckill.model.entity.User;

/**
 * 保存当前用户
 */
public class UserContext {

    private static ThreadLocal<User> userHolder = new ThreadLocal<User>();

    public static void setUser(User user) {
        userHolder.set(user);
    }

    public static User getUser() {
        return userHolder.get();
    }

}
