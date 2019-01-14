package com.mmall.dao;

import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    User selectByNameAndPwd(@Param("username") String username, @Param("password") String password);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int updateByUsernameSelective(User record);

    int selectUsernameCount(String username);

    int selectEmailCount(String email);

    int selectCountByPwdAndId(@Param("password") String password, @Param("id") Integer id);

    String selectQuestion(String username);

    int selectAnswerCount(@Param("username")String username,@Param("question") String question, @Param("answer")String answer);

    int selectCountByEmailAndId(@Param("email")String email, @Param("id")Integer id);

}