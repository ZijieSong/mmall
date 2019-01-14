package com.mmall;

import com.mmall.common.ServerResponse;
import com.mmall.controller.portal.UserController;
import com.mmall.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.sql.SQLOutput;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
        "file:src/main/webapp/WEB-INF/dispatcher-servlet.xml" })
public class ServiceTest {
    @Resource(name = "userService")
    UserService userService;
    @Autowired
    UserController userController;

    @Test
    public void testUserService(){
        ServerResponse serverResponse = userService.login("admin","admin");
        System.out.println(serverResponse.getStatus());
    }

    @Test
    public void testUserController(){
        ServerResponse serverResponse =userController.getForgetQuestion("admin");
        System.out.println(serverResponse.getStatus());
    }
}
