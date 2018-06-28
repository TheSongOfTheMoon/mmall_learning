package com.mmall.controller.backend;

import com.mmall.common.Conts;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.schema.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/manager/user/")
@Slf4j
public class UserManagerController {

    //private static final Logger logger= LoggerFactory.getLogger(UserManagerController.class);

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username,String password,HttpSession session){
        log.info("后台登录");
        ServerResponse<User>  response=iUserService.login(username,password);
        log.info("后台登录-成功");
        if (response.isSuccess()){
            User  user=response.getData();
           if (user.getRole()== Conts.Role.ROLE_ADMIN){
               //登录的是管理员
               session.setAttribute(Conts.CURRENT_USER,user);
               return ServerResponse.createBySuccess("登录成功",user);
           }else{
               return ServerResponse.createByErrorMessage("不是管理员，无法登录");
           }
        }
        return response;
    }





}
