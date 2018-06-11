package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Conts;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.service.IOrderService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;


    //管理端的后台(订单列表)
    @RequestMapping("getOrderList.do")
    @ResponseBody
    public ServerResponse<PageInfo>  orderList(HttpSession session, @RequestParam(value ="pageNum",defaultValue ="1")int pageNum,@RequestParam(value ="pageSize",defaultValue ="10")int pageSize){
        //已经登录
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
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
    @RequestMapping("getOrderDetail.do")
    @ResponseBody
    public ServerResponse<OrderVo>  getOrderDetail(HttpSession session, Long orderNo){
        //已经登录
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
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
    @RequestMapping("getOrderDetail.do")
    @ResponseBody
    public ServerResponse<PageInfo>  SearchOrder(HttpSession session, Long orderNo, @RequestParam(value ="pageNum",defaultValue ="1")int pageNum,@RequestParam(value ="pageSize",defaultValue ="10")int pageSize){
        //已经登录
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
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
    @RequestMapping("sendOrderGoods.do")
    @ResponseBody
    public ServerResponse<String>  sendOrderGoods(HttpSession session, Long orderNo){
        //已经登录
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
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
