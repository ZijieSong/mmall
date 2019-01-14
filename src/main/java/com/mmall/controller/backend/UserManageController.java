package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Resource(name = "userService")
    UserService userService;

    @RequestMapping(value="login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = userService.login(username, password);
        if(!response.isSuccess())
            return response;
        if(response.getData().getRole() != Const.role.ROLE_ADMIN)
            return ServerResponse.fail("该用户不是管理员，无法登陆");
        session.setAttribute(Const.CURRENT_USER,response.getData());
        return response;
    }
}
