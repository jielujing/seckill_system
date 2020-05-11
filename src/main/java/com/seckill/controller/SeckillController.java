package com.seckill.controller;

import com.seckill.interceptor.AccessLimit;
import com.seckill.model.entity.SeckillOrder;
import com.seckill.model.entity.User;
import com.seckill.model.rediskey.SeckillKey;
import com.seckill.mq.MQSender;
import com.seckill.model.dto.SeckillMessageDTO;
import com.seckill.model.entity.CodeMsg;
import com.seckill.model.entity.Result;
import com.seckill.model.rediskey.GoodsKey;
import com.seckill.redis.RedisService;
import com.seckill.service.GoodsService;
import com.seckill.service.SeckillService;
import com.seckill.service.SeckillUserService;
import com.seckill.service.OrderService;
import com.seckill.model.dto.GoodsDTO;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * @author Dell
 * @create 2019-07-21 14:06
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    MQSender sender;

    private HashMap<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 系统初始化
     * 系统启动时就把商品数量加载到缓存中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsDTO> goodsList = goodsService.getGoodsDTOList();
        if (goodsList == null)
            return;
        for (GoodsDTO goodsDTO : goodsList) {
            redisService.set(GoodsKey.SeckillGoodsStock, "" + goodsDTO.getId(), goodsDTO.getStockCount());
            goodsDTO.setStockCount(null);
            goodsDTO.setGoodsStock(null);
            redisService.set(GoodsKey.GoodsDetail,""+goodsDTO.getId(),goodsDTO);
            localOverMap.put(goodsDTO.getId(), false);
        }
    }

    /**
     * 秒杀
     */
    @RequestMapping(value="/{path}/do_seckill", method=RequestMethod.POST)
    @ResponseBody
    public Result<Integer> seckill(Model model, User user,
                                   @RequestParam("goodsId")long goodsId,
                                   @PathVariable("path") String path) {
        model.addAttribute("user", user);
        //验证path
        boolean check = seckillService.checkPath(user, goodsId, path);
        if(!check){
            return Result.error(CodeMsg.VERITY_CODE_ERROR);
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if(over) {
            return Result.error(CodeMsg.GOODS_COUNT_OVER);
        }
        // 缓存预减库存
        long stock = redisService.decr(GoodsKey.SeckillGoodsStock, ""+goodsId);//10
        if(stock < 0) { // 若库存小于0，则直接返回
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.GOODS_COUNT_OVER);
        }
        // 判断是否已经秒杀过了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        // 入队
        SeckillMessageDTO mm = new SeckillMessageDTO();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        sender.sendSeckillMessage(mm);
        return Result.success(0);
    }

    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     * */
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(User user,
                                      @RequestParam("goodsId")long goodsId) {
        long result = seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(result);
    }

    /**
     * 获得path
     * @param request
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    @AccessLimit(seconds = 5, maxCount = 5)
    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPath(HttpServletRequest request, User user,
                                         @RequestParam("goodsId")long goodsId,
                                         @RequestParam(value="verifyCode", defaultValue="0")int verifyCode) {
        //校验验证码
        boolean check = seckillService.checkVerifyCode(user, goodsId, verifyCode);
        if(!check) {
            return Result.error(CodeMsg.VERITY_CODE_ERROR);
        }
        String path = seckillService.createPath(user, goodsId);
        return Result.success(path);
    }

    /**
     * 获得验证码
     * @param response
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillVerifyCode(HttpServletResponse response, User user,
                                              @RequestParam("goodsId")long goodsId) {
        try {
            BufferedImage image = seckillService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch(Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }
    }

    @PostMapping("/getVerifyCode")
    @ResponseBody
    public Result<String> getVerifyCode(HttpServletResponse response,User user, @RequestParam("goodsId")long goodsId){
        String verifyCode=redisService.get(SeckillKey.SeckillVerifyCode,user.getId()+","+goodsId,String.class);
        return Result.success(verifyCode);
    }


}

