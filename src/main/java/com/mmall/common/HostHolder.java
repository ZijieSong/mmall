package com.mmall.common;

import com.mmall.pojo.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    private ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public void addUser(User user){
        userThreadLocal.set(user);
    }

    public User getUser(){
        return userThreadLocal.get();
    }

    public void removeUser(){
        userThreadLocal.remove();
    }
}
