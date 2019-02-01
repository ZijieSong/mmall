package com.mmall.controller;

import com.mmall.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class netappTest extends BaseController{
    @Resource
    private CartService cartService;

    private static Logger logger =LoggerFactory.getLogger(netappTest.class);

    @RequestMapping("test.do")
    @ResponseBody
    public String test() throws Exception {

        logger.info("first logger");
        logger.info("second logger");
        logger.error("first error");

        throw new NullPointerException("nullpointer!!");
    }

    @RequestMapping("paramTest.do")
    @ResponseBody
    public void paramtersTest(HttpServletRequest request, Integer id){
        Map map = request.getParameterMap();
        String value = request.getParameter("id");
        return;
    }

    @RequestMapping("/mmall/cookpath.do")
    @ResponseBody
    public void cookPath(HttpServletResponse response){
        Cookie cookie = new Cookie("test","szj");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @RequestMapping("/mmall/getCookie.do")
    @ResponseBody
    public void getCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for(Cookie c: cookies)
            System.out.println(c.getName()+":"+c.getValue());
    }

    @RequestMapping("/mall/getCookie.do")
    @ResponseBody
    public void getCookieMa(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for(Cookie c: cookies)
            System.out.println(c.getName()+":"+c.getValue());
    }

}
