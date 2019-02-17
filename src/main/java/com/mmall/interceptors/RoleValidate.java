package com.mmall.interceptors;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.HostHolder;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.ShardedRedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public class RoleValidate implements HandlerInterceptor {

    @Autowired
    HostHolder hostHolder;
    @Resource(name = "userService")
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) o;

        //操作handlerMethod获取各请求信息
        //getBean()是获取访问的controller的bean，getClass获取controller相应的class对象
        String className = handlerMethod.getBean().getClass().getSimpleName();
        //getMethod获取访问的方法
        String methodName = handlerMethod.getMethod().getName();

        Map<String, String[]> requestParameters = httpServletRequest.getParameterMap();
        StringBuffer sb = new StringBuffer();
        requestParameters.forEach((k, v) -> sb.append(k).append("=").append(Arrays.toString(v)).append(";"));

        if (StringUtils.equals(className, "UserManageController") && StringUtils.equals(methodName, "login")) {
            log.info("class name: {}, method name:{}", className, methodName);
            return true;
        }

        log.info("class name: {}, method name: {}, request parameters: {}", className, methodName, sb.toString());

        //重置response
        httpServletResponse.reset();
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");

        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotEmpty(loginToken)) {
            User user = JsonUtil.stringToObject(ShardedRedisUtil.get(Const.RedisKey.LOGIN_TOKEN_PREFIX + loginToken), User.class);
            if (user != null) {
                if (userService.checkRole(user).isSuccess()) {
                    hostHolder.addUser(user);
                    return true;
                } else {
                    if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")) {
                        Map<String, Object> map = Maps.newHashMap();
                        map.put("success",false);
                        map.put("msg","用户无权限");
                        output(httpServletResponse,map);
                    } else {
                        ServerResponse serverResponse = ServerResponse.fail("用户无权限");
                        output(httpServletResponse, serverResponse);
                    }
                    return false;
                }
            }
        }

        if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("success",false);
            map.put("msg","用户需登录");
            output(httpServletResponse,map);
        } else {
            ServerResponse serverResponse = ServerResponse.fail("用户需登录");
            output(httpServletResponse, serverResponse);
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.removeUser();
    }

    private void output(HttpServletResponse httpServletResponse, Object object) throws IOException {
        PrintWriter printWriter = httpServletResponse.getWriter();
        printWriter.write(JsonUtil.objToString(object));
        printWriter.flush();
        printWriter.close();
    }
}
