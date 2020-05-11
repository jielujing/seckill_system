package com.seckill.mq;

import com.seckill.model.entity.SeckillOrder;
import com.seckill.model.entity.User;
import com.seckill.model.dto.SeckillMessageDTO;
import com.seckill.redis.RedisService;
import com.seckill.service.GoodsService;
import com.seckill.service.SeckillService;
import com.seckill.service.OrderService;
import com.seckill.model.dto.GoodsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Dell
 * @create 2019-07-28 15:03
 */
@Service
public class MQReceiver {

    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    /**
     * Direct模式，Exchange交换机
     * @param message
     */
    @RabbitListener(queues=MQConfig.SECKILL_QUEUE)
    public void receive(String message) {
        logger.info("receive message:" + message);
        SeckillMessageDTO mm = RedisService.stringToBean(message, SeckillMessageDTO.class);
        User user = mm.getUser();
        long goodsId = mm.getGoodsId();

        //判断库存
        GoodsDTO goods = goodsService.getGoodsByGoodsId(goodsId); //这里是从数据库中判断
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return;
        }

       // 判断是否已经秒杀过了
       SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
       if(order != null) {
           return;
       }

       //写入秒杀订单
       seckillService.seckill(user, goods);
   }
}
