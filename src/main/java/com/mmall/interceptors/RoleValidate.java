package com.mmall.interceptors;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import org.codehaus.jackson.map.ObjectMapper;
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

        httpServletResponse.setContentType("application/json; charset=utf-8");
        httpServletResponse.setCharacterEncoding("utf-8");

        HttpSession session = httpServletRequest.getSession();
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            ServerResponse serverResponse = ServerResponse.fail("用户需登录");
            httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(serverResponse));
            return false;
        }
        if(!userService.checkRole(user).isSuccess()){
            ServerResponse serverResponse = ServerResponse.fail("用户无权限");
            httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(serverResponse));
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
