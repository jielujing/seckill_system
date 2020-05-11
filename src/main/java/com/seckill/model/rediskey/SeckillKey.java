package com.seckill.model.rediskey;

/**
 * @author Dell
 * @create 2019-07-28 21:03
 */
public class SeckillKey extends BasePrefix {
    private SeckillKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SeckillKey GoodsOver = new SeckillKey(0, "goodsOver");
    public static SeckillKey SeckillPath = new SeckillKey(60, "seckillPath");
    public static SeckillKey SeckillVerifyCode = new SeckillKey(300, "seckillVerifyCode");
}
