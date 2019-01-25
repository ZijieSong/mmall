package com.mmall.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class netappTest extends BaseController{

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
}
