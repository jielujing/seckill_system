package com.seckill.model.dto;

import com.seckill.model.entity.OrderInfo;

/**
 * @author Dell
 * @create 2019-07-24 18:08
 */
public class OrderDetailDTO {
    private GoodsDTO goods;
    private OrderInfo order;
    public GoodsDTO getGoods() {
        return goods;
    }
    public void setGoods(GoodsDTO goods) {
        this.goods = goods;
    }
    public OrderInfo getOrder() {
        return order;
    }
    public void setOrder(OrderInfo order) {
        this.order = order;
    }
}
