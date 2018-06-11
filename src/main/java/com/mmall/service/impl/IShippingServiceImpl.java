package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iShippingService")//方便被Controller注入到
public class IShippingServiceImpl implements IShippingService {


    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse<Map> add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount=shippingMapper.insert(shipping);
        if (rowCount>0){
            Map result= Maps.newHashMap();
            result.put("shipping",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功", result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }


    public ServerResponse<String> del(Integer userId, Integer shippingId){
        int rowCount=shippingMapper.deleteByShippingIdUserId(userId,shippingId);
        if (rowCount>0){
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }


    public ServerResponse<String> update(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount=shippingMapper.updateByShipping(shipping);
        if (rowCount>0){
            return ServerResponse.createBySuccessMessage("修改地址成功");
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }


    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        Shipping shipping=shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if (shipping==null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.createBySuccess("新建地址失败",shipping);
    }


    public ServerResponse<PageInfo> selectlist(Integer userId, Integer pageNum, Integer pageSize){
        PageHelper.offsetPage(pageNum,pageSize);
        List<Shipping> shipping=shippingMapper.selectByUserId(userId);
        PageInfo pageInfo=new PageInfo(shipping);
        return ServerResponse.createBySuccess(pageInfo);
    }


}
