package com.mmall.dao;

import com.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUserIdAndOrder(@Param("userId")Integer userId,@Param("orderNo")Long orderNo);

    Order selectByOrderNo(@Param("orderNo")long orderNo);

    List<Order> selectByUserId(Integer userId);

    List<Order> selectAll();

    //查询出某种状态下的订单
    List<Order>  selectOrderStatusByCreateTime(@Param("status")Integer status,@Param("date")String date);

    //关闭订单
    int closeOrderByOrderId(Integer id);
}