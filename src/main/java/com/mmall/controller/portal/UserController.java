package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.ShardedRedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Resource(name = "userService")
    private UserService userService;

    /**
     * 登陆接口
     *
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password,
                                      HttpServletResponse servletResponse, HttpSession session) {
        ServerResponse<User> serverResponse = userService.login(username, password);
        if (serverResponse.isSuccess()) {
//            session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
            String userJson = JsonUtil.objToString(serverResponse.getData());
            ShardedRedisUtil.setex(Const.RedisKey.LOGIN_TOKEN_PREFIX + session.getId(), userJson, Const.EXPIRE_TIME.LOGIN_TOKEN_EXPIRE);
            log.info("redis:key:{},value:{}",session.getId(),userJson);
            CookieUtil.generateLoginCookie(servletResponse, session.getId());
        }
        return serverResponse;
    }

    /**
     * 登出
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse logout(HttpServletRequest request, HttpServletResponse response) {
//        session.removeAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken != null) {
            ShardedRedisUtil.del(Const.RedisKey.LOGIN_TOKEN_PREFIX + loginToken);
            CookieUtil.delLoginToken(request, response);
        }
        return ServerResponse.success();
    }


    @RequestMapping(value = "/register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse register(User user) {
        return userService.register(user);
    }

    @RequestMapping(value = "/check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkValid(String str, String type) {
        return userService.checkValid(str, type);
    }

    @RequestMapping(value = "/get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request, HttpServletResponse response) {
//        User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken != null) {
            User user = JsonUtil.stringToObject(ShardedRedisUtil.get(Const.RedisKey.LOGIN_TOKEN_PREFIX + loginToken), User.class);
            if (user != null)
                return ServerResponse.successByData(user);
        }
        return ServerResponse.fail("用户未登陆");
    }

    @RequestMapping(value = "/forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getForgetQuestion(String username) {
        return userService.getForgetQuestion(username);
    }

    @RequestMapping(value = "/forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        return userService.checkAnswer(username, question, answer);
    }

    @RequestMapping(value = "/forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse resetForgetPassword(String username, String newPassword, String token) {
        return userService.resetForgetPassword(username, newPassword, token);
    }

    @RequestMapping(value = "/reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse resetPassword(String oldPassword, String newPassword, HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken != null) {
            User user = JsonUtil.stringToObject(ShardedRedisUtil.get(Const.RedisKey.LOGIN_TOKEN_PREFIX + loginToken), User.class);
            if (user != null)
                return userService.resetPassword(oldPassword, newPassword, user);
        }
        return ServerResponse.fail("用户未登陆");
    }

    @RequestMapping(value = "/update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpServletRequest request, User user) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken != null) {
            User currentUser = JsonUtil.stringToObject(ShardedRedisUtil.get(Const.RedisKey.LOGIN_TOKEN_PREFIX + loginToken), User.class);
            if (currentUser != null) {
                user.setId(currentUser.getId());
                user.setUsername(currentUser.getUsername());
                ServerResponse<User> serverResponse = userService.updateInformation(user);
                if (!serverResponse.isSuccess())
                    return serverResponse;
                //更新成功在session中添加新的用户信息
                ShardedRedisUtil.setex(Const.RedisKey.LOGIN_TOKEN_PREFIX + loginToken, JsonUtil.objToString(serverResponse.getData()), Const.EXPIRE_TIME.LOGIN_TOKEN_EXPIRE);
                return serverResponse;
            }
        }
        return ServerResponse.fail("用户未登陆");
    }

    @RequestMapping(value = "/get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken != null) {
            User user = JsonUtil.stringToObject(ShardedRedisUtil.get(Const.RedisKey.LOGIN_TOKEN_PREFIX + loginToken), User.class);
            if (user != null)
                return userService.getInformation(user.getId());
        }
        return ServerResponse.fail("用户未登陆");
    }

}
