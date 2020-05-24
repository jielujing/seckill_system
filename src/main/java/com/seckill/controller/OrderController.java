package com.seckill.controller;

import com.seckill.model.entity.User;
import com.seckill.model.entity.OrderInfo;
import com.seckill.redis.RedisService;
import com.seckill.model.entity.CodeMsg;
import com.seckill.model.entity.Result;
import com.seckill.service.GoodsService;
import com.seckill.service.SeckillUserService;
import com.seckill.service.OrderService;
import com.seckill.model.dto.GoodsDTO;
import com.seckill.model.dto.OrderDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Dell
 * @create 2019-07-24 17:43
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailDTO> info(@RequestParam("orderId") long orderId) {

        OrderInfo order = orderService.getOrderById(orderId);
        if (order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }

        long goodsId = order.getGoodsId();
        GoodsDTO goods = goodsService.getGoodsByGoodsId(goodsId);
        OrderDetailDTO orderDeatail = new OrderDetailDTO();
        orderDeatail.setOrder(order);
        orderDeatail.setGoods(goods);
        return Result.success(orderDeatail);
    }
}
