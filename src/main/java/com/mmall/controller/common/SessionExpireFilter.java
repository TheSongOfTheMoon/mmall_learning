package com.mmall.controller.common;

import com.mmall.common.Conts;
import com.mmall.pojo.User;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JacksonUtil;
import com.mmall.utils.RedisPoolUtil;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest  req=(HttpServletRequest)request;
        String loginToken= CookieUtil.readLoginToken(req);
        //判空
        if (StringUtils.isNotEmpty(loginToken)){

            String userStr= RedisPoolUtil.getJedis(loginToken);
            User user= JacksonUtil.StrToObject(userStr,User.class);
            if (user!=null){
                //更新时间
                RedisPoolUtil.setExpireJedis(loginToken, Conts.RedisCacheExtime.REDIS_SESSION_EXTIME);
            }
        }
        chain.doFilter(request,response);

    }

    @Override
    public void destroy() {

    }
}
