package com.seckill.controller;

import com.seckill.model.dto.LoginDTO;
import com.seckill.model.entity.User;
import com.seckill.redis.RedisService;
import com.seckill.model.entity.Result;
import com.seckill.service.SeckillUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Dell
 * @create 2019-07-21 20:59
 */
@Controller
public class UserController {
    public static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    SeckillUserService seckillUserService;

    @RequestMapping("/login/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/login/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, LoginDTO loginDTO) {
        //log.info(loginDTO.toString());
        // 登录
        seckillUserService.login(response, loginDTO);
        return Result.success(true);
    }

    @RequestMapping("/user/info")
    @ResponseBody
    public Result<User> info(Model model, User user) {
        return Result.success(user);
    }

}
