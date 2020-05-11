package com.seckill.model.dao;

import com.seckill.model.entity.SeckillOrder;
import com.seckill.model.entity.OrderInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @author Dell
 * @create 2019-07-21 14:33
 */
@Mapper
@Repository
public interface OrderDao {

    @Select("select * from seckill_order where user_id=#{userId} and goods_id=#{goodsId}")
    SeckillOrder getSeckillOrderByUserIdGoodsId(@Param("userId")long userId, @Param("goodsId")long goodsId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    long insert(OrderInfo orderInfo);

    @Insert("insert into seckill_order (user_id, goods_id, order_id) values (#{userId}, #{goodsId}, #{orderId})")
    int insertSeckillOrder(SeckillOrder seckillOrder);

    @Select("select * from order_info WHERE id=#{orderId} ")
    OrderInfo selectOrderById(@Param("orderId") long orderId);
}
