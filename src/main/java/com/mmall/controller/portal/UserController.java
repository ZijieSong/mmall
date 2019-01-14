package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
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
@RequestMapping("/user")
public class UserController {
    @Resource(name = "userService")
    private UserService userService;

    /**
     * 登陆接口
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> serverResponse = userService.login(username,password);
        if(serverResponse.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
        }
        return serverResponse;
    }

    /**
     * 登出
     * @param session
     * @return
     */
    @RequestMapping(value = "/logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.success();
    }


    @RequestMapping(value = "/register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse register(User user){
        return userService.register(user);
    }

    @RequestMapping(value = "/check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkValid(String str, String type){
        return userService.checkValid(str, type);
    }

    @RequestMapping(value = "/get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.fail("用户未登陆");
        return ServerResponse.successByData(user);
    }

    @RequestMapping(value = "/forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getForgetQuestion(String username){
        return userService.getForgetQuestion(username);
    }

    @RequestMapping(value = "/forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkAnswer(String username, String question, String answer){
        return userService.checkAnswer(username, question, answer);
    }

    @RequestMapping(value = "/forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse resetForgetPassword(String username,String newPassword, String token){
        return userService.resetForgetPassword(username, newPassword, token);
    }

    @RequestMapping(value = "/reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse resetPassword(String oldPassword, String newPassword, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null)
            return ServerResponse.fail("用户未登录");
        return userService.resetPassword(oldPassword,newPassword,user);
    }

    @RequestMapping(value = "/update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session, User user){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null)
            return ServerResponse.fail("用户未登录");
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> serverResponse = userService.updateInformation(user);
        if(!serverResponse.isSuccess())
            return serverResponse;

        //更新成功在session中添加新的用户信息
        session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
        return serverResponse;
    }

    @RequestMapping(value = "/get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.fail(ResponseCode.NEED_LOGIN.getStatus(),"用户未登录");
        return userService.getInformation(user.getId());
    }

}
