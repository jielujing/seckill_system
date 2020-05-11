package com.seckill.model.dto;

import com.seckill.model.entity.User;

/**
 * @author Dell
 * @create 2019-07-24 15:42
 */
public class GoodsDetailDTO {
    private int seckillStatus = 0;
    private int remainSeconds = 0;
    private GoodsDTO goods ;
    private User user;

    public int getSeckillStatus() {
        return seckillStatus;
    }

    public void setSeckillStatus(int seckillStatus) {
        this.seckillStatus = seckillStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsDTO getGoods() {
        return goods;
    }

    public void setGoods(GoodsDTO goods) {
        this.goods = goods;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
