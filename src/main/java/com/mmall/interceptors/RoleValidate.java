package com.mmall.interceptors;

import com.mmall.common.Const;
import com.mmall.common.HostHolder;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.ShardedRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RoleValidate implements HandlerInterceptor {

    @Autowired
    HostHolder hostHolder;
    @Resource(name = "userService")
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        httpServletResponse.setContentType("application/json; charset=utf-8");

        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(loginToken!=null){
            User user = JsonUtil.stringToObject(ShardedRedisUtil.get(Const.RedisKey.LOGIN_TOKEN_PREFIX+loginToken),User.class);
            if(user!=null){
                if(userService.checkRole(user).isSuccess()){
                    hostHolder.addUser(user);
                    return true;
                }else{
                    ServerResponse serverResponse = ServerResponse.fail("用户无权限");
                    httpServletResponse.getWriter().write(JsonUtil.objToString(serverResponse));
                    return false;
                }
            }
        }
        ServerResponse serverResponse = ServerResponse.fail("用户需登录");
        httpServletResponse.getWriter().write(JsonUtil.objToString(serverResponse));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.removeUser();
    }
}
