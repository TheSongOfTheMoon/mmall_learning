package com.mmall.controller.backend;

import com.mmall.common.Conts;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JacksonUtil;
import com.mmall.utils.RedisShardedJedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager/user/")
@Slf4j
public class UserManagerController {

    //private static final Logger logger= LoggerFactory.getLogger(UserManagerController.class);

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username, String password,HttpSession session,HttpServletResponse httpservletResponse){
        log.info("后台登录");
        ServerResponse<User>  response=iUserService.login(username,password);
        log.info("后台登录-成功");
        if (response.isSuccess()){
            User  user=response.getData();
           if (user.getRole()== Conts.Role.ROLE_ADMIN){
               //登录的是管理员
               session.setAttribute(Conts.CURRENT_USER,user);
               CookieUtil.writeLoginToken(httpservletResponse,session.getId());
               RedisShardedJedisPoolUtil.setExJedis(session.getId(),Conts.RedisCacheExtime.REDIS_SESSION_EXTIME, JacksonUtil.objToString(response.getData()));

               return ServerResponse.createBySuccess("登录成功",user);
           }else{
               return ServerResponse.createByErrorMessage("不是管理员，无法登录");
           }
        }
        return response;
    }





}
