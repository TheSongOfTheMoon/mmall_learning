package com.mmall.dao;

import com.mmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> getByOrderNoByUserId(@Param("orderNo")Long orderNo,@Param("userId")Integer userId);

    List<OrderItem> getByOrderNoBy(@Param("orderNo")Long orderNo);

    void BatchInsert(@Param("orderItemList")List<OrderItem> orderItemList);
}