package com.mmall.controller.common;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.ShardedRedisUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionResetFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String loginToken = CookieUtil.readLoginToken((HttpServletRequest) servletRequest);
        if(loginToken!=null){
            User user = JsonUtil.stringToObject(ShardedRedisUtil.get(Const.RedisKey.LOGIN_TOKEN_PREFIX+loginToken),User.class);
            if(user!=null)
                ShardedRedisUtil.expire(Const.RedisKey.LOGIN_TOKEN_PREFIX+loginToken,Const.EXPIRE_TIME.LOGIN_TOKEN_EXPIRE);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
