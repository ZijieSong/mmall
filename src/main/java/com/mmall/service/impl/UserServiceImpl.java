package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.util.MD5Util;
import com.mmall.util.ShardedRedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        if (userMapper.selectUsernameCount(username) == 0) {
            return ServerResponse.fail("用户不存在");
        }
        User user = userMapper.selectByNameAndPwd(username, MD5Util.MD5EncodeUtf8(password));
        if (user == null) {
            return ServerResponse.fail("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.successByData(user);
    }

    @Override
    public ServerResponse register(User user) {
        if(!checkValid(user.getUsername(),Const.USERNAME).isSuccess())
            return ServerResponse.fail( "用户名已存在");
        if(!checkValid(user.getEmail(),Const.EMAIL).isSuccess())
            return ServerResponse.fail("邮箱已存在");
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        if(userMapper.insertSelective(user)<1)
            return ServerResponse.fail("注册失败");
        return ServerResponse.successByMsg("注册成功");
    }

    @Override
    public ServerResponse checkValid(String str, String type) {
        if(StringUtils.isBlank(str) || StringUtils.isBlank(type))
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(),"参数不能为空");
        if (StringUtils.equals(type, Const.USERNAME) && userMapper.selectUsernameCount(str) > 0)
            return ServerResponse.fail("用户名已存在");
        if (StringUtils.equals(type, Const.EMAIL) && userMapper.selectEmailCount(str) > 0)
            return ServerResponse.fail("邮箱已存在");
        return ServerResponse.successByMsg("校验成功");
    }

    @Override
    public ServerResponse<String> getForgetQuestion(String username) {
        if(checkValid(username,Const.USERNAME).isSuccess())
            return ServerResponse.fail("用户不存在");
        String question = userMapper.selectQuestion(username);
        if(StringUtils.isBlank(question))
            return ServerResponse.fail("未设置问题");
        return ServerResponse.successByData(question);
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        if(checkValid(username,Const.USERNAME).isSuccess())
            return ServerResponse.fail("用户不存在");
        if(userMapper.selectAnswerCount(username, question, answer)<1)
            return ServerResponse.fail("问题答案错误");
        String token = UUID.randomUUID().toString();
        ShardedRedisUtil.setex(Const.RedisKey.RESET_PASSWORD_PREFIX+username, token, Const.EXPIRE_TIME.PASSRESET_TOKEN_EXPIRE);
        return ServerResponse.successByData(token);
    }

    @Override
    public ServerResponse resetForgetPassword(String username, String newPassword, String token) {
        if(checkValid(username,Const.USERNAME).isSuccess())
            return ServerResponse.fail("用户不存在");
        if(StringUtils.isBlank(token))
            return ServerResponse.fail("token入参失败");
        if(!StringUtils.equals(token,ShardedRedisUtil.get(Const.RedisKey.RESET_PASSWORD_PREFIX+username)))
            return ServerResponse.fail("token无效");
        User user = new User();
        user.setUsername(username);
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        if(userMapper.updateByUsernameSelective(user)<1)
            return ServerResponse.fail("重置密码失败");
        return ServerResponse.successByMsg("重置密码成功");
    }

    @Override
    public ServerResponse resetPassword(String oldPassword, String newPassword, User user) {
        if(userMapper.selectCountByPwdAndId(MD5Util.MD5EncodeUtf8(oldPassword),user.getId())==0)
            return ServerResponse.fail("密码错误");
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        if(userMapper.updateByPrimaryKeySelective(user)<1)
            return ServerResponse.fail("修改密码失败");
        return ServerResponse.successByMsg("修改密码成功");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        //username不能更改
        //mail不能重复，注意与当前登陆用户进行区分
        if(userMapper.selectCountByEmailAndId(user.getEmail(),user.getId())>0)
            return ServerResponse.fail("email已存在");
        User userUpdate = new User();
        userUpdate.setId(user.getId());
        userUpdate.setEmail(user.getEmail());
        userUpdate.setQuestion(user.getQuestion());
        userUpdate.setAnswer(user.getAnswer());
        userUpdate.setPhone(user.getPhone());

        if(userMapper.updateByPrimaryKeySelective(userUpdate)<1)
            return ServerResponse.fail("更新失败");

        userUpdate.setUsername(user.getUsername());
        return ServerResponse.success("更新成功", userUpdate);
    }

    @Override
    public ServerResponse<User> getInformation(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null)
            return ServerResponse.fail("用户不存在");
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.successByData(user);
    }

    @Override
    public ServerResponse checkRole(User user) {
        if(user!=null && user.getRole() == Const.Role.ROLE_ADMIN)
            return ServerResponse.success();
        return ServerResponse.fail();
    }


}
