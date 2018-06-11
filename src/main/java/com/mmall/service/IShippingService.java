package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

import java.util.Map;

public interface IShippingService {
    public ServerResponse<Map> add(Integer userId, Shipping shipping);

    public ServerResponse<String> del(Integer userId, Integer shippingId);

    public ServerResponse<String> update(Integer userId, Shipping shipping);

    public ServerResponse<Shipping> select(Integer userId, Integer shippingId);

    public ServerResponse<PageInfo> selectlist(Integer userId, Integer pageNum, Integer pageSize);
}
