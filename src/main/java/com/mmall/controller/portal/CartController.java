package com.mmall.controller.portal;


import com.mmall.common.Conts;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.service.IUserService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JacksonUtil;
import com.mmall.utils.RedisShardedJedisPoolUtil;
import com.mmall.vo.CartVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value ="/cart")
@Slf4j
public class CartController {

    /*一个Cart是一个记录，相同的用户ID为一个购物车*/

    @Autowired
    private ICartService iCartService;

    private IUserService iUserService;


    @RequestMapping(value ="listCart.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> listCart(HttpServletRequest httpServletRequest/*HttpSession session*/) {
        //User user = (User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user= JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return iCartService.list(user.getId());
    }


    @RequestMapping(value ="addCart.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> addCart(HttpServletRequest httpServletRequest/*HttpSession session*/, Integer count, Integer productId){
        if (count==null||productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //User user=(User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }


    //更新购物车
    @RequestMapping(value ="updateCart.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> updateCart(HttpServletRequest httpServletRequest/*HttpSession session*/, Integer count, Integer productId){
        if (count==null||productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //User user=(User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //更新购物车
        return iCartService.update(user.getId(),productId,count);
    }

    //删除产品
    //更新购物车
    @RequestMapping(value ="deleteCart.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> deleteCart(HttpServletRequest httpServletRequest/*HttpSession session*/, String productIds){
        if (productIds==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //User user=(User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //更新购物车
        return iCartService.delete(user.getId(),productIds);
    }


    //全选
    @RequestMapping(value ="selectAll.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpServletRequest httpServletRequest/*HttpSession session*/, Integer count, Integer productId){
        if (count==null||productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //User user=(User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //更新购物车
        return iCartService.selectAllOrUnselectAll(user.getId(),Conts.Cart.CHECKED);
    }


    //全反选
    @RequestMapping(value ="UnSelectAll.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> UnselectAll(HttpServletRequest httpServletRequest/*HttpSession session*/, Integer count, Integer productId){
        if (count==null||productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //User user=(User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //更新购物车
        return iCartService.selectAllOrUnselectAll(user.getId(),Conts.Cart.UNCHECK);
    }



    //全单选
    @RequestMapping(value ="select.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> select(HttpServletRequest httpServletRequest/*HttpSession session*/, Integer count, Integer productId){
        if (count==null||productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //User user=(User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //更新购物车
        return iCartService.selectAllOrUnselect(user.getId(),productId,Conts.Cart.CHECKED);
    }


    //反单选
    @RequestMapping(value ="UnSelect.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> Unselect(HttpServletRequest httpServletRequest/*HttpSession session*/, Integer count, Integer productId){
        if (count==null||productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //User user=(User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //更新购物车
        return iCartService.selectAllOrUnselect(user.getId(),productId,Conts.Cart.UNCHECK);
    }


    //获取当前购物车中产品数量
    //全选
    @RequestMapping(value ="get_cart_product_count.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpServletRequest httpServletRequest/*HttpSession session*/, Integer count, Integer productId){
        //User user=(User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user==null){
            return ServerResponse.createBySuccess(0);
        }
        //更新购物车
        return iCartService.getCartProductCount(user.getId());
    }



}
