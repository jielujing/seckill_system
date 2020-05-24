package com.seckill.service;

import com.seckill.interceptor.UserContext;
import com.seckill.model.dao.UserDao;
import com.seckill.model.entity.User;
import com.seckill.model.exception.GlobalException;
import com.seckill.model.rediskey.UserKey;
import com.seckill.redis.RedisService;
import com.seckill.model.entity.CodeMsg;
import com.seckill.util.MD5Util;
import com.seckill.util.UUIDUtil;
import com.seckill.model.dto.LoginDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dell
 * @create 2019-07-18 19:21
 */
@Service
public class SeckillUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    UserDao userDao;

    @Autowired
    RedisService redisService;

    /**
     * 用户登录
     */
    public boolean login(HttpServletResponse response, LoginDTO loginDTO) {
        if (loginDTO == null)
            throw new GlobalException(CodeMsg.SERVER_SERROR);
        //获得用户信息
        String mobile = loginDTO.getMobile();
        User user = getById(Long.parseLong(mobile));
        if (user == null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calPass = MD5Util.formPassToDBPass(loginDTO.getPassword(), saltDB);
        if (!calPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //更新用户token
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return true;
    }

    /**
     * 根据用户的id获得用户
     */
    public User getById(long id) {
        // 从缓存中取
        User user = redisService.get(UserKey.Id, "" + id, User.class);
        if (user != null) {
            return user;
        }
        // 从数据库中取
        user = userDao.selectUserById(id);
        //将用户加入到缓存
        if (user != null)
            redisService.set(UserKey.Id, "" + id, user);
        return user;
    }

    /**
     * 根据用户的token获得用户对象
     *
     * @param response
     * @param token
     * @return
     */
    public User getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token))
            return null;

        User user = redisService.get(UserKey.Token, token, User.class);
        //延长有效期
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;
    }

    public void addCookie(HttpServletResponse response, String token, User user) {
        //生成cookie
        redisService.set(UserKey.Token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(UserKey.Token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
