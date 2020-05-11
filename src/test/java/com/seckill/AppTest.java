package com.seckill;

import static org.junit.Assert.assertTrue;

import com.seckill.model.dao.GoodsDao;
import com.seckill.model.dao.UserDao;
import com.seckill.model.dto.GoodsDTO;
import com.seckill.model.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTest{
    @Autowired
    GoodsDao goodsDao;

    @Autowired
    UserDao userDao;

    @Test
    public void shouldAnswerWithTrue()
    {

//        for(int i=3;i<=100;i++){
//            GoodsDTO goodsDTO=new GoodsDTO();
//            goodsDTO.setId(Long.valueOf(i));
//            goodsDTO.setGoodsName("iphone"+i);
//            goodsDTO.setGoodsDetail("Apple iphone"+i);
//            goodsDTO.setGoodsImg("/img/iphonex.img");
//            goodsDTO.setGoodsPrice(new BigDecimal(9999));
//            goodsDTO.setGoodsStock(100);
//            goodsDTO.setSeckillPrice(new BigDecimal(9999));
//            goodsDTO.setStockCount(100);
//            goodsDTO.setStartDate(new Date());
//            goodsDTO.setEndDate(new Date(System.currentTimeMillis()+1000000000));
//            goodsDao.insertGoods(goodsDTO);
//            goodsDao.insertSeckillGoods(goodsDTO);
//        }

        for(int i=1;i<=100;i++){
            User user=new User();
            user.setPassword("b7797cce01b4b131b433b6acf4add449");
            user.setId(Long.valueOf("123456789"+i));
            user.setSalt("1a2b3c4d");
            user.setNickName("jack");
            userDao.insertUser(user);
        }
    }
}

