package com.seckill.service;

import com.seckill.model.dao.GoodsDao;
import com.seckill.model.entity.SeckillGoods;
import com.seckill.model.dto.GoodsDTO;
import com.seckill.model.rediskey.GoodsKey;
import com.seckill.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Dell
 * @create 2019-07-18 19:21
 */
@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    @Autowired
    RedisService redisService;



    //商品列表
    public List<GoodsDTO> getGoodsDTOList() {
        return goodsDao.selectGoodsDTOList();
    }

    // 根据goodsId查找goods
    public GoodsDTO getGoodsByGoodsId(long goodsId) {
        GoodsDTO goodsDTO=redisService.get(GoodsKey.GoodsDetail,""+goodsId,GoodsDTO.class);
        if(goodsDTO==null){
            goodsDTO=goodsDao.selectGoodsDTOByGoodsId(goodsId);
            redisService.set(GoodsKey.GoodsDetail,""+goodsId,goodsDTO);
        }
        goodsDTO.setStockCount(redisService.get(GoodsKey.SeckillGoodsStock,""+goodsId,Integer.class));
        return goodsDTO;
    }

    /**
     * 减少库存，处理MQ信息时调用
     * @param goods
     * @return
     */
    public boolean reduceStock(GoodsDTO goods) {
        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setGoodsId(goods.getId());
        int count = goodsDao.reduceStock(seckillGoods);
        return count > 0;
    }
}
