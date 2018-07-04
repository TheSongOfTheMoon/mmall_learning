package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import javax.print.attribute.standard.Severity;
import java.util.Map;

public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);
    ServerResponse alipayCallback(Map<String,String> params);
    ServerResponse queryOrderByStatus(Integer userId,Long orderNo);
    ServerResponse  CreateOrder(Integer userId,Integer shippingId);
    //取消订单
    ServerResponse<String>  cancelOrder(Integer userId,Long orderNo);
    //查询产品
    ServerResponse getOrderCartProduct(Integer userId);
    //订单详情
    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    //个人中心查询订单
    ServerResponse<PageInfo> getOrderList(Integer userId, int pageSize, int pageNum);

    //后台获取订单
    ServerResponse<PageInfo>  manageList(int pageNum,int pageSize);

    //根据订单号获取订单详情
    ServerResponse<OrderVo>  manageOrderDetail(Long orderNo);

    //根据订单号搜索
    ServerResponse<PageInfo>  manageOrderSearch(Long orderNo, int pageSize, int pageNum);

    //订单号发货
    ServerResponse<String>  manageSendGoods(Long orderNo);

    /*======================================定时任务关单===========================================*/
    void CloseOrderTask(int hour);
}
