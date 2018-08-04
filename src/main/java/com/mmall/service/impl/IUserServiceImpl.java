package com.mmall.service.impl;

import com.mmall.common.Conts;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.utils.MD5Util;
import com.mmall.utils.RedisShardedJedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")//声明成一个service可以被controller调用,向上注入
@Slf4j
public class IUserServiceImpl implements IUserService {


    //日志跟踪
    //private Logger logger= LoggerFactory.getLogger(IProductServiceImpl.class);

    @Autowired
    protected UserMapper userMapper;


    public ServerResponse<User> login(String username, String password) {

        log.info("日志跟踪");
        //检验用户名是否存在
        int resultCount=userMapper.checkUserName(username);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        // MD5加密  可以通过MD5碰撞解除
        password=MD5Util.MD5EncodeUtf8(password);
        //匹配密码用户
        User user=userMapper.selectLogin(username,password);
        if (user==null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);

    }

    //校验用户名是否存在
    public ServerResponse<String> register2(User user){
        //检验用户名是否存在
        int resultCount=userMapper.checkUserName(user.getUsername());
        if (resultCount>0){
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        resultCount=userMapper.checkUserEmail(user.getEmail());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("Email已经存在");
        }

        user.setRole(Conts.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        resultCount=userMapper.insert(user);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }


    public ServerResponse<String> register(User user){
        ServerResponse<String> valueresponseName=this.checkValue(user.getUsername(),Conts.USERNAME);
        if (!valueresponseName.isSuccess()){
             return valueresponseName;
        }

        ServerResponse<String> valueResponseEmail=this.checkValue(user.getEmail(),Conts.EMAIL);
        if (!valueResponseEmail.isSuccess()){
            return valueResponseEmail;
        }

        user.setRole(Conts.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount=userMapper.insert(user);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }




    //校验数据是否存在
    public ServerResponse<String> checkValue(String str,String type){
        if (org.apache.commons.lang3.StringUtils.isNotBlank(str)){
            int resultCount=0;
            if(Conts.USERNAME.equals(type)){
                resultCount=userMapper.checkUserName(str);
                if (resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名已经存在");
                }
            }
            if (Conts.EMAIL.equals(type)){
                resultCount=userMapper.checkUserEmail(str);
                if (resultCount>0){
                    return ServerResponse.createByErrorMessage("邮箱已经存在");
                }
            }

        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    //忘记密码找回密码问题
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse<String> valueResponse=this.checkValue(username,Conts.USERNAME);
        if (valueResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question=userMapper.selectQuestion(username);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }

        return ServerResponse.createByErrorMessage("找回密码的问题不存在");
    }

    //检查问题答案

    public ServerResponse<String> CheckAnswer(String username, String question, String answer){
        int resultCount=userMapper.CheckAnswer(username,question,answer);
        if (resultCount>0){
            String forgetToken= UUID.randomUUID().toString();//生成一个唯一标识的ID
            //TokenCache.setKey("Token_"+username,forgetToken);//生成一个可供认识的标识，并放入缓存中

            //以前放置在GuavaCache中，现在放置在Redis中
            RedisShardedJedisPoolUtil.setExJedis("Token_"+username,60*60*12,forgetToken);

            return ServerResponse.createBySuccess(forgetToken);//放入一个对象
        }
        return ServerResponse.createByErrorMessage("问题答案错误!");
    }

    //重置密码问题
    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        ServerResponse  valueResponse=this.checkValue(username,Conts.USERNAME);
        if (valueResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //String token=TokenCache.getKey("token_"+username);

        log.info("'尝试获取forgetoken'");
        String token= RedisShardedJedisPoolUtil.getJedis("Token_"+username);
        if (org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("Token无效,可能过期");
        }

        if (org.apache.commons.lang3.StringUtils.equals(forgetToken,token)){
            String mdPassword=MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount=userMapper.UpdatePasswordByName(username,mdPassword);
            if (resultCount>0){
                return ServerResponse.createBySuccessMessage("修改成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新获取");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");

    }

    //登录状态下更新密码
    public ServerResponse<String> checkResetPassword(String passwordOld,String passwordNew,User user){
        //为了防止横向越权，需要检验旧密码
        String passowrdmd5=MD5Util.MD5EncodeUtf8(passwordOld);
        int resultCount=userMapper.CheckRestPassword(passowrdmd5,user.getId());
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount=userMapper.updateByPrimaryKeySelective(user);
        if (updateCount>0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");

    }


    //更新个人信息
    public ServerResponse<User> updateInformation(User user){
        //将要邮箱
        int resultCount=userMapper.CheckEmail(user.getEmail(),user.getId());
        if (resultCount>0){
            return ServerResponse.createByErrorMessage("邮箱已经存在，请重新输入");
        }
        User updateuser=new User();
        updateuser.setId(user.getId());//更新相同
        updateuser.setEmail(user.getEmail());
        updateuser.setPhone(user.getPhone());
        updateuser.setQuestion(user.getQuestion());
        updateuser.setAnswer(user.getAnswer());
        int resultCoount=userMapper.updateByPrimaryKeySelective(updateuser);

        if (resultCoount>0){
            return ServerResponse.createBySuccess("更新信息成功",updateuser);
        }
        return ServerResponse.createByErrorMessage("更新信息失败");
    }

    //获取详细信息
    public ServerResponse<User>  getInformation(Integer userId){
        User user=userMapper.selectByPrimaryKey(userId);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("获取用户信息成功",user);
    }


    //管理员模块:存在并且获取数据
    public ServerResponse<String> checkAdminRole(User user){
        if (user!=null && user.getRole().intValue()==Conts.Role.ROLE_ADMIN){
                return ServerResponse.createBySuccessMessage("确认是管理员身份");
        }else{
            return ServerResponse.createByErrorMessage("认证管理员失败");
        }
    }
}