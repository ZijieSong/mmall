package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.ShardedRedisUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Resource(name = "userService")
    private UserService userService;

    @RequestMapping(value="login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpServletResponse servletResponse, HttpSession session){
        ServerResponse<User> response = userService.login(username, password);
        if (response.isSuccess()) {
            if(response.getData().getRole() != Const.Role.ROLE_ADMIN)
                return ServerResponse.fail("该用户不是管理员，无法登陆");
            String userJson = JsonUtil.objToString(response.getData());
            ShardedRedisUtil.setex(Const.RedisKey.LOGIN_TOKEN_PREFIX + session.getId(), userJson, Const.EXPIRE_TIME.LOGIN_TOKEN_EXPIRE);
            CookieUtil.generateLoginCookie(servletResponse, session.getId());
        }
        return response;
    }
}
