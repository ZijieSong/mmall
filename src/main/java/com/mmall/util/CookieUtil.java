package com.mmall.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
    private static final String LOGIN_TOKEN_NAME = "login_token";
    private static final String LOGIN_TOKEN_DOMAIN = PropertiesUtil.get("login.token.domain");

    public static void generateLoginCookie(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(LOGIN_TOKEN_NAME,token);
        //设置在跟路径下，即访问任何路径都会带上cookie
        cookie.setPath("/");
        //设置在一级域名下，访问www.mmall.com或user.mmall.com等都可以带上cookie
        cookie.setDomain(LOGIN_TOKEN_DOMAIN);
        //设置cookie的过期时间，秒
        cookie.setMaxAge(60*60*24);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);
    }

    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), LOGIN_TOKEN_NAME))
                    return cookie.getValue();
            }
        }
        return null;
    }

    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie:cookies){
            if(StringUtils.equals(cookie.getName(),LOGIN_TOKEN_NAME)){
                //设置成0，代表删除此cookie。
                cookie.setMaxAge(0);
                //读到的cookie只有name和value不为空，其余均为null，要想成功覆盖，还需设置path和domain
                cookie.setPath("/");
                cookie.setDomain(LOGIN_TOKEN_DOMAIN);
                response.addCookie(cookie);
                return;
            }
        }
    }
}
