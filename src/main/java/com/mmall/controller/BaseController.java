package com.mmall.controller;

import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import org.codehaus.jackson.map.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;


@Controller
public class BaseController {

    private static Logger logger = LoggerFactory.getLogger(BaseController.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServerResponse handleException(Exception exception){
        Map<String, Object> map = Maps.newHashMap();
        map.put("status",500005);
        map.put("msg","系统有点累");
        logger.error("exception info: ",exception);
        return ServerResponse.fail((int)map.get("status"),(String)map.get("msg"));
    }
}
