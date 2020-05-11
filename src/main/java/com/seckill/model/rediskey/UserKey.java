package com.seckill.model.rediskey;

/**
 * @author Dell
 * @create 2019-07-19 22:33
 */
public class UserKey extends BasePrefix {
    public static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    public static UserKey Token = new UserKey(TOKEN_EXPIRE, "token");
    public static UserKey Id = new UserKey(0, "id");

    public UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

}
