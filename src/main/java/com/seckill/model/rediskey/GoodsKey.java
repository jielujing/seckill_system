package com.seckill.model.rediskey;

/**
 * @author Dell
 * @create 2019-07-17 19:52
 */
public class GoodsKey extends BasePrefix {

    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKey GoodsDetail = new GoodsKey(0, "goodsDetail");

    public static GoodsKey GoodsList = new GoodsKey(2000,"goodsList");
    public static GoodsKey SeckillGoodsStock = new GoodsKey(0,"seckillGoodsStock");
}
