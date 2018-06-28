package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String username);
    //指定userMapper.xml的SQL传入的参数必须是名字跟参数里面一模一样
    User selectLogin(@Param("username")String username,@Param("password")String password);

    int checkUserEmail(String email);

    String selectQuestion(String username);

    int CheckAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);

    //根据姓名改密码
    int UpdatePasswordByName(@Param("username")String username,@Param("password")String password);

    int CheckRestPassword(@Param("password")String password ,@Param("userId")Integer userId);

    int CheckEmail(@Param("email_old")String email_old,@Param("userId")Integer userId);
}