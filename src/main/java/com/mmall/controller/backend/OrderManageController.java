package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.service.IOrderService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JacksonUtil;
import com.mmall.utils.RedisShardedJedisPoolUtil;
import com.mmall.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMethod;
@Controller
@RequestMapping("/manage/order")
@Slf4j
public class OrderManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;


    //管理端的后台(订单列表)
    @RequestMapping(value = "getOrderList.do",method =RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo>  orderList(HttpServletRequest httpServletRequest/*HttpSession session*/, @RequestParam(value ="pageNum",defaultValue ="1")int pageNum, @RequestParam(value ="pageSize",defaultValue ="10")int pageSize){
        //已经登录
        //User user= (User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user= JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登录");
        }
        //已有权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            //追加产品的业务逻辑
            return iOrderService.manageList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无操作权限");
        }
    }



    //管理端的后台(详情)
    @RequestMapping(value = "getOrderDetail.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<OrderVo>  getOrderDetail(HttpServletRequest httpServletRequest/*HttpSession session*/, Long orderNo){
        //已经登录
        //User user= (User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登录");
        }
        //已有权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            //追加产品的业务逻辑
            return iOrderService.manageOrderDetail(orderNo);
        }else{
            return ServerResponse.createByErrorMessage("无操作权限");
        }
    }


    //管理端的后台(搜索)
    @RequestMapping(value = "getSearchOrder.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo>  SearchOrder(HttpServletRequest httpServletRequest/*HttpSession session*/, Long orderNo, @RequestParam(value ="pageNum",defaultValue ="1")int pageNum,@RequestParam(value ="pageSize",defaultValue ="10")int pageSize){
        //已经登录
        //User user= (User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登录");
        }
        //已有权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            //追加产品的业务逻辑
            return iOrderService.manageOrderSearch(orderNo,pageSize,pageNum);
        }else{
            return ServerResponse.createByErrorMessage("无操作权限");
        }
    }


    //管理端的后台(发货)
    @RequestMapping(value = "sendOrderGoods.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>  sendOrderGoods(HttpServletRequest httpServletRequest/*HttpSession session*/, Long orderNo){
        //已经登录
        //User user= (User) session.getAttribute(Conts.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
        User user=JacksonUtil.StrToObject(strLoginToken,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登录");
        }
        //已有权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            //追加产品的业务逻辑
            return iOrderService.manageSendGoods(orderNo);
        }else{
            return ServerResponse.createByErrorMessage("无操作权限");
        }
    }



}
