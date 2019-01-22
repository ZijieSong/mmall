package com.mmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class netappTest {
    @RequestMapping("test.do")
    @ResponseBody
    public String test(){
        return "hello world!";
    }
}
