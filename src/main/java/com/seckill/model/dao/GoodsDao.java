package com.seckill.model.dao;

import com.seckill.model.entity.SeckillGoods;
import com.seckill.model.dto.GoodsDTO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Dell
 * @create 2019-07-21 0:46
 */
@Mapper
@Repository
public interface GoodsDao {

    @Select("select g.*,sg.stock_count, sg.start_date, sg.end_date,sg.seckill_price from seckill_goods sg left join goods g on sg.goods_id = g.id")
    List<GoodsDTO> selectGoodsDTOList();

    @Select("select g.*,sg.stock_count, sg.start_date, sg.end_date,sg.seckill_price from seckill_goods sg left join goods g on sg.goods_id = g.id where g.id=#{goodsId} ")
    GoodsDTO selectGoodsDTOByGoodsId(@Param("goodsId") long goodsId);

    @Update("update seckill_goods sg, goods g set sg.stock_count=sg.stock_count-1,g.goods_stock=g.goods_stock-1 where sg.goods_id=#{goodsId} and sg.stock_count>0 and g.goods_stock>0 and sg.goods_id=g.id")
    int reduceStock(SeckillGoods g);

    @Update("update seckill_goods set stock_count = #{stockCount} where goods_id = #{goodsId}")
    int resetStock(SeckillGoods g);

    @Insert("insert into goods values (default,#{goodsName},#{goodsTitle},#{goodsImg},#{goodsDetail},#{goodsPrice},#{goodsStock})")
    int insertGoods(GoodsDTO goodsDTO);

    @Insert("insert into seckill_goods values (default,#{id},#{seckillPrice},#{stockCount},#{startDate},#{endDate})")
    int insertSeckillGoods(GoodsDTO goodsDTO);
}
