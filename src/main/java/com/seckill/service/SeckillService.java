package com.seckill.service;

import com.seckill.model.entity.SeckillOrder;
import com.seckill.model.entity.User;
import com.seckill.model.entity.OrderInfo;
import com.seckill.model.rediskey.SeckillKey;
import com.seckill.redis.RedisService;
import com.seckill.util.MD5Util;
import com.seckill.util.UUIDUtil;
import com.seckill.model.dto.GoodsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author Dell
 * @create 2019-07-21 14:27
 */
@Service
public class SeckillService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    /**
     * 向数据库写入订单信息
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo seckill(User user, GoodsDTO goods) {
        //减库存
        boolean success = goodsService.reduceStock(goods);
        if(success) {
            //写入订单信息和秒杀订单
            return orderService.createOrder(user, goods);
        }else {
            //库存不足
            setGoodsOver(goods.getId());
            return null;
        }
    }

    /**
     * 查询订单是否完成
     * @param userId
     * @param goodsId
     * @return
     */
    public long getSeckillResult(Long userId, long goodsId) {
        // 从缓存中查找是否有秒杀订单
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsId);
        if(order != null) {  // 秒杀成功,返回订单id
            return order.getOrderId();
        }else { // 当秒杀订单为空时，两种情况，1-库存不足，2-正在排队
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1; //库存不足
            }else {
                return 0; //正在排队
            }
        }
    }

    /**
     * 记录库存信息
     * @param goodsId
     */
    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.GoodsOver, "" + goodsId, true);
    }

    /**
     * 获得库存是否充足
     * @param goodsId
     * @return
     */
    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.GoodsOver, "" + goodsId);
    }

    /**
     * 验证path
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    public boolean checkPath(User user, long goodsId, String path) {
        if(user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(SeckillKey.SeckillPath, ""+user.getId() + "_"+ goodsId, String.class);
        return path.equals(pathOld);
    }

    /**
     * 生成path
     * @param user
     * @param goodsId
     * @return
     */
    public String createPath(User user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(SeckillKey.SeckillPath, ""+user.getId() + "_"+ goodsId, str);
        return str;
    }

    /**
     * 生成验证码
     * @param user
     * @param goodsId
     * @return
     */
    public BufferedImage createVerifyCode(User user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDC3843));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(SeckillKey.SeckillVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    /**
     * 校验验证码
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    public boolean checkVerifyCode(User user, long goodsId, int verifyCode) {
        if(user == null || goodsId <=0) {
            return false;
        }
        Integer codeOld = redisService.get(SeckillKey.SeckillVerifyCode, user.getId()+","+goodsId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(SeckillKey.SeckillVerifyCode, user.getId()+","+goodsId);
        return true;
    }

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }
}
