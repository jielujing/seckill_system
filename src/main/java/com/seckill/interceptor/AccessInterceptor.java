package com.seckill.interceptor;

import com.seckill.model.entity.User;
import com.seckill.model.rediskey.AccessKey;
import com.seckill.redis.RedisService;
import com.seckill.model.entity.CodeMsg;
import com.seckill.model.entity.Result;
import com.seckill.service.SeckillUserService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 限制用户登录次数
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println(request.getRequestURI());
        if (handler instanceof HandlerMethod) {
            User user = getUser(request, response);
            if(user==null){
                responseError(response, CodeMsg.SESSION_ERROR);
                return false;
            }
            UserContext.setUser(user);

            //获得controller的注解信息
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            String key = request.getRequestURI()+"_" + user.getId();

            //判断一定时间内的登录次数，如果小于maxCount，就在缓存中+1
            AccessKey accessKey = AccessKey.withExpire(seconds);
            Integer count = redisService.get(accessKey, key, Integer.class);
            if(count  == null) {
                redisService.set(accessKey, key, 1);
            }else if(count < maxCount) {
                redisService.incr(accessKey, key);
            }else {
                responseError(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }

        return true;
    }

    private void responseError(HttpServletResponse response, CodeMsg cm) throws IOException {
        response.setContentType("applicaton/json;charset=UTF-8");
        OutputStream outputStream = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm));

        outputStream.write(str.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 获得当前登录的用户
     * @param request
     * @param response
     * @return
     */
    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(SeckillUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, SeckillUserService.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken))
            return null;

        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        //根据token获得用户
        return userService.getByToken(response, token);
    }

    /**
     * 获得cookie
     * @param request
     * @param cookiName
     * @return
     */
    private String getCookieValue(HttpServletRequest request, String cookiName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0)
            return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookiName))
                return cookie.getValue();
        }
        return null;
    }
}
