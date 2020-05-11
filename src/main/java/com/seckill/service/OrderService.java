package com.seckill.service;

import com.seckill.model.dao.OrderDao;
import com.seckill.model.entity.SeckillOrder;
import com.seckill.model.entity.User;
import com.seckill.model.entity.OrderInfo;
import com.seckill.model.rediskey.OrderKey;
import com.seckill.redis.RedisService;
import com.seckill.model.dto.GoodsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Dell
 * @create 2019-07-21 14:23
 */
@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    /**
     * 从redis获得订单
     * @param userId
     * @param goodsId
     * @return
     */
    public SeckillOrder getSeckillOrderByUserIdGoodsId(long userId, long goodsId) {
        return redisService.get(OrderKey.getSeckillOrderByUidGid, "" + userId + "_" + goodsId, SeckillOrder.class);
    }

    /**
     * 插入订单
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo createOrder(User user, GoodsDTO goods) {
        // 生成订单并插入
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderDao.insert(orderInfo);

        // 生成秒杀订单并插入
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());
        orderDao.insertSeckillOrder(seckillOrder);

        redisService.set(OrderKey.getSeckillOrderByUidGid, "" + user.getId() + "_" + goods.getId(), seckillOrder);

        return orderInfo;
    }

    /**
     * 根据id查找订单
     * @param orderId
     * @return
     */
    public OrderInfo getOrderById(long orderId) {
        return orderDao.selectOrderById(orderId);
    }

}
