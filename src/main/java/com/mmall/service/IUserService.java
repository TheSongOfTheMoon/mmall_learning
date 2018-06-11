package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValue(String str,String type);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> CheckAnswer(String username,String question,String answer);

    ServerResponse<String> forgetRestPassword(String username,String password,String token);

    ServerResponse<String> checkResetPassword(String passwordOld,String passwordNew,User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User>  getInformation(Integer userId);

    /*管理员部分*/
    ServerResponse<String> checkAdminRole(User user);
}

