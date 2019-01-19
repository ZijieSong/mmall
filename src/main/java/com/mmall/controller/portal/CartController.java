package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {

    @RequestMapping(value = "add.do")
    @ResponseBody
    public ServerResponse addCart(HttpSession httpSession, Integer productId, Integer count){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);

    }
}
