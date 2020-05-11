package com.seckill.controller;

import com.seckill.model.entity.User;
import com.seckill.model.rediskey.GoodsKey;
import com.seckill.redis.RedisService;
import com.seckill.model.entity.Result;
import com.seckill.service.GoodsService;
import com.seckill.service.SeckillUserService;
import com.seckill.model.dto.GoodsDetailDTO;
import com.seckill.model.dto.GoodsDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Dell
 * @create 2019-07-19 23:00
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    SeckillUserService userService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    /**
     * 页面缓存
     */
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response, Model model, User user) {
        model.addAttribute("user", user);
        // 查询商品列表
        List<GoodsDTO> goodsList = goodsService.getGoodsDTOList();
        model.addAttribute("goodsList", goodsList);

        // 取缓存
        String html = redisService.get(GoodsKey.GoodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        // 手动渲染
        WebContext springWebContext = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", springWebContext);
        if (!StringUtils.isEmpty(html))
            redisService.set(GoodsKey.GoodsList, "", html);

        return html;
    }

    /**
     * 获得商品详情
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value="/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailDTO> detail(User user, @PathVariable("goodsId")long goodsId) {
        GoodsDTO goods = goodsService.getGoodsByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int seckillStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {  // 秒杀还没开始，倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){ // 秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailDTO goodsDetailDTO = new GoodsDetailDTO();
        goodsDetailDTO.setGoods(goods);
        goodsDetailDTO.setUser(user);
        goodsDetailDTO.setRemainSeconds(remainSeconds);
        goodsDetailDTO.setSeckillStatus(seckillStatus);
        Result<GoodsDetailDTO> result=Result.success(goodsDetailDTO);
        return result;
    }
}
