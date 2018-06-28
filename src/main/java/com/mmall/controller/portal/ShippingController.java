package com.mmall.controller.portal;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Conts;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/shipping/")
@Slf4j
public class ShippingController {

    //private static Logger loggr= LoggerFactory.getLogger(ShippingController.class);


    @Autowired
    private IShippingService iShippingService;

    //新增地址
    @RequestMapping(value ="addShipping.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Map> add(HttpSession session, Shipping shipping){
        log.info("开始新增");
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //添加
        log.info("开始新增-调用服务层方法");
        return iShippingService.add(user.getId(),shipping);

    }


    //删除地址
    @RequestMapping(value ="del.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> del(HttpSession session, Integer shippingId){
        log.info("开始删除地址");
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        log.info("删除地址-调用服务层方法");
        //添加
        return iShippingService.del(user.getId(),shippingId);

    }

    //更新地址
    @RequestMapping(value ="update.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> update(HttpSession session, Shipping shipping){
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return iShippingService.update(user.getId(),shipping);

    }


    //查询
    @RequestMapping(value ="select.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Shipping> select(HttpSession session, Integer shippingId){
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        log.info("进行查询的服务层方法");
        return iShippingService.select(user.getId(),shippingId);
    }

    @RequestMapping(value ="PageInfo.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> select(HttpSession session,
                                           @RequestParam(value="pageNum", defaultValue="1")Integer pageNum,
                                           @RequestParam(value="pageSize", defaultValue="10")Integer pageSize){
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return iShippingService.selectlist(user.getId(),pageNum,pageSize);
    }

}
