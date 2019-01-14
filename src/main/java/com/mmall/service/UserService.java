package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface UserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse register(User user);

    ServerResponse checkValid(String str, String type);

    ServerResponse<String> getForgetQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse resetForgetPassword(String username, String newPassword, String token);

    ServerResponse resetPassword(String oldPassword,String newPassword,User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer id);

    ServerResponse checkRole(User user);
}
