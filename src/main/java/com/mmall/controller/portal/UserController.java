package com.mmall.controller.portal;

import com.mmall.common.Conts;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JacksonUtil;
import com.mmall.utils.RedisShardedJedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller                 //设置为了让springmvc的扫描器可以扫描
@RequestMapping("/user/")   //将所有的控制请求都归属到用户模块去
@Slf4j
public class UserController {

    @Autowired
    protected IUserService iUserService;

    //用户登录
    @RequestMapping(value = "userlogin.do", method = RequestMethod.GET) //制定请求的类型和方式
    @ResponseBody //制定返回数据的时候以json形式返回
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpservletResponse, HttpServletRequest httpservletRequest) {
        log.info("=================================登录================================");
        //开始调用mybatis调dao层
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            //session.setAttribute(Conts.CURRENT_USER, response.getData());//整个User对象存入session
            CookieUtil.writeLoginToken(httpservletResponse,session.getId());
            RedisShardedJedisPoolUtil.setExJedis(session.getId(),Conts.RedisCacheExtime.REDIS_SESSION_EXTIME, JacksonUtil.objToString(response.getData()));
            return response;
        }else{
            return ServerResponse.createByErrorMessage("登录失败");
        }
    }

    //退出
    @RequestMapping(value = "loginout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> loginout(HttpServletResponse httpservletResponse, HttpServletRequest httpservletRequest) {
        String loginToken=CookieUtil.readLoginToken(httpservletRequest);
        CookieUtil.delLoginToken(httpservletRequest,httpservletResponse);
        RedisShardedJedisPoolUtil.delJedis(loginToken);

        //session.removeAttribute(Conts.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    //注册
    @RequestMapping(value = "register.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    //校验不为空
    @RequestMapping(value = "checkValue.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> checkValue(String str, String type) {
        return iUserService.checkValue(str, type);
    }

    //获取用户信息
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session,HttpServletRequest httpServletRequest) {
        //User user = (User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }else{
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }

    }

    //获取忘记密码问题
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
         return iUserService.selectQuestion(username);

    }

    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.CheckAnswer(username,question,answer);
    }

    //重置密码
    @RequestMapping(value = "forget_rest_password.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String usrname,String passwordNew,String forgetToken){
        return iUserService.forgetRestPassword(usrname,passwordNew,forgetToken);
    }

    //修改密码
    @RequestMapping(value = "reset_password.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> reSetPassword(HttpServletRequest httpServletRequest/*HttpSession session*/,String passwordOld,String passwordNew){
        //User user = (User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        return iUserService.checkResetPassword(passwordOld,passwordNew,user);
    }

    //更新用户信息
    @RequestMapping(value = "update_information.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpServletRequest httpServletRequest/*HttpSession session*/, User user){
        //User currentuser = (User) session.getAttribute(Conts.CURRENT_USER);//多人存储
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User currentuser=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (currentuser == null) {
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        user.setId(currentuser.getId());//使用从数据库获取的Id,防止横向越权
        user.setUsername(currentuser.getUsername());//从当前中获取
        ServerResponse<User> response=iUserService.updateInformation(user);
        if (response.isSuccess()){
            //session.setAttribute(Conts.CURRENT_USER,response.getData());
            //set的数据只要key值不变允许覆盖
            RedisShardedJedisPoolUtil.setExJedis(strLoginToken,Conts.RedisCacheExtime.REDIS_SESSION_EXTIME,JacksonUtil.objToString(response.getData()));
        }
        return response;
    }

    //获取用户信息
    @RequestMapping(value = "get_Information.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getInformation(HttpServletRequest httpServletRequest/*HttpSession session*/){
        //User currentuser=(User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User currentuser=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (currentuser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }

        return iUserService.getInformation(currentuser.getId());
    }

}
