package com.mmall.interceptors;

import com.mmall.common.Const;
import com.mmall.common.HostHolder;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.ShardedRedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public class LoginValidate implements HandlerInterceptor {

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) o;

        String className = handlerMethod.getBean().getClass().getSimpleName();
        String methodName = handlerMethod.getMethod().getName();

        Map<String, String[]> requestParams = httpServletRequest.getParameterMap();
        StringBuffer sb = new StringBuffer();
        requestParams.forEach((k, v) -> sb.append(k).append("=").append(Arrays.toString(v)).append(";"));

        if (StringUtils.equals(className, "UserController") && StringUtils.equals(methodName, "login")) {
            log.info("class name:{}, method name:{}", className, methodName);
            return true;
        }
        log.info("class name:{}, method name:{}, requestParams:{}", className, methodName, sb.toString());

        httpServletResponse.reset();
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");

        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (loginToken != null) {
            User user = JsonUtil.stringToObject(ShardedRedisUtil.get(Const.RedisKey.LOGIN_TOKEN_PREFIX + loginToken), User.class);
            if (user != null) {
                hostHolder.addUser(user);
                return true;
            }
        }
        ServerResponse serverResponse = ServerResponse.fail(ResponseCode.NEED_LOGIN.getStatus(), "用户未登录");
        output(httpServletResponse,serverResponse);
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
