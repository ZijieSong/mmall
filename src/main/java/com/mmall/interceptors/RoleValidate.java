package com.mmall.interceptors;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RoleValidate implements HandlerInterceptor {

    @Resource(name = "userService")
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        httpServletResponse.setHeader("Content-type", "text/html;charset=UTF-8");
        HttpSession session = httpServletRequest.getSession();
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            httpServletResponse.getWriter().write("用户需登陆");
            return false;
        }
        if(!userService.checkRole(user).isSuccess()){
            httpServletResponse.getWriter().write("用户无权限");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
