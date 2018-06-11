package com.mmall.controller.portal;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Conts;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping(value ="/Shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    //新增地址
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<Map> add(HttpSession session, Shipping shipping){
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //添加
        return iShippingService.add(user.getId(),shipping);

    }


    //删除地址
    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse<String> del(HttpSession session, Integer shippingId){
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //添加
        return iShippingService.del(user.getId(),shippingId);

    }

    //更新地址
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<String> update(HttpSession session, Shipping shipping){
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return iShippingService.update(user.getId(),shipping);

    }


    //查询
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(HttpSession session, Integer shippingId){
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return iShippingService.select(user.getId(),shippingId);
    }

    @RequestMapping("PageInfo.do")
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
