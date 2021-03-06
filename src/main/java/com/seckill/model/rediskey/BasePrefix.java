package com.seckill.model.rediskey;

/**
 * @author Dell
 * @create 2019-07-17 19:47
 */
public abstract class BasePrefix{

    private int expireSeconds; // 过期时间
    private String prefix; // 前缀

    public BasePrefix(String prefix) { // 0代表永不过期
        this(0, prefix);
    }

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public int expireSeconds() { // 默认0代表永不过期
        return expireSeconds;
    }

    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }
}
