package com.seckill.model.dto;

import com.seckill.model.entity.User;

/**
 * @author Dell
 * @create 2019-07-28 19:09
 */
public class SeckillMessageDTO {
    private User user;
    private long goodsId;
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public long getGoodsId() {
        return goodsId;
    }
    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
