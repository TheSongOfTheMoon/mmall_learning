package com.mmall.dao;

import com.mmall.pojo.Cart;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    //整个对象插入
    int insert(Cart record);
    //根据传进去的对象是否有值,判断对象是否为空,不为空放进SQL中
    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    //根据逐渐更新
    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);
}