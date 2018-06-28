package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Conts;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;



@Controller
@RequestMapping("/order/")
@Slf4j
public class OrderController {


    //private static final Logger loggr= LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService IOrderService;




    //查询产品
    @RequestMapping("getOrderCartProduct.do")
    @ResponseBody
    public ServerResponse<String> getOrderCartProduct(HttpSession session){
        User user=(User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        return IOrderService.getOrderCartProduct(user.getId());
    }



    //订单详情
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session, Long orderNo){
        User user=(User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        return IOrderService.getOrderDetail(user.getId(),orderNo);
    }


    //个人中心查看订单
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse CenterList(HttpSession session, @RequestParam(value = "pageSize",defaultValue ="10")int pageSize, @RequestParam(value = "pageNum",defaultValue ="1")int pageNum){
        User user=(User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        return IOrderService.getOrderList(user.getId(),pageSize,pageNum);
    }







    //创建订单
    @RequestMapping("CreateOrder.do")
    @ResponseBody
    public ServerResponse<String> CreateOrder(HttpSession session, Integer shippingId){
        User user=(User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //上下文
        String path=session.getServletContext().getRealPath("upload");
        return IOrderService.CreateOrder(user.getId(),shippingId);
    }



    //取消订单
    @RequestMapping("CanncelOrder.do")
    @ResponseBody
    public ServerResponse<String> CanncelOrder(HttpSession session,Long orderNo){
        User user=(User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //上下文
        String path=session.getServletContext().getRealPath("upload");
        return IOrderService.cancelOrder(user.getId(),orderNo);
    }





    //支付订单
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse<String> pay(HttpSession session, Long orderNum, HttpServletRequest request){
        User user=(User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        log.info("开始支付订单");
                            //上下文
        String path=session.getServletContext().getRealPath("upload");
        log.info("开始支付订单2");
        return IOrderService.pay(orderNum,user.getId(),path);
    }


    //支付宝回调
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map requestParams=request.getParameterMap();
        Map<String,String> params= Maps.newHashMap();

        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
            String name=(String)iter.next();
            String[] vlues=(String[])requestParams.get(name);
            String valueStr="";
            for (int i=0;i<vlues.length;i++){
                valueStr=(i==vlues.length-1)?valueStr+vlues[i]:",";
            }
            params.put(name,valueStr);
        }
        log.info("支付宝回调: sing:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        //验证回调正确性,避免重复通知

        params.remove("sign_type");
        boolean alipayRSACheckedV2= false;
        try {
            alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getPublicKey(),"utf-8",Configs.getSignType());
            if (!alipayRSACheckedV2){
                return ServerResponse.createByErrorMessage("非法请求不通过，再恶意请求我就找网警了");
            }else{
                log.info("支付宝回调成功，开始回调服务");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝回调异常:"+e.getMessage());
            e.printStackTrace();
        }
        log.info("支付宝回调成功，开始回调服务");
        //todo
        ServerResponse  serverresponse= IOrderService.alipayCallback(params);
        log.info("支付宝回调成功，回调服务成功!");
        if (serverresponse.isSuccess()){
            return Conts.AlipayCallBack.RESPONSE_SUCCESS;
        }else{
            return Conts.AlipayCallBack.RESPONSE_FAILED;
        }
    }



    //支付宝定时查询
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNum, HttpServletRequest request){
        User user=(User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //上下文
        ServerResponse  serverResponse= IOrderService.queryOrderByStatus(user.getId(),orderNum);
        if (serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}
