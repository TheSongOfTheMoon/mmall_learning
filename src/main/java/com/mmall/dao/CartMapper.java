package com.mmall.dao;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    //整个对象插入
    int insert(Category record);
    //根据传进去的对象是否有值,判断对象是否为空,不为空放进SQL中
    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    //根据逐渐更新
    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Cart record);

    List<Category> selectCategoryChildrenByCategoryId(Integer categoryId);

    //查询购物车
    Cart selectCartByUserIdandProductId(@Param("userId")Integer userId, @Param("productId")Integer productId);

    //查找购物车
    List<Cart> selectCartByUserId(@Param("userId")Integer userId);

    int selectCartCheckStatusByUserId(Integer userid);

    int deleteByUserIdProductIds(@Param("userId")Integer userId, @Param("productIdList")List<String> productIdList);


    int checkOrUncheckAllProduct(@Param("userId")Integer userId, @Param("checked")Integer checked);

    int checkOrUncheckProduct(@Param("userId")Integer userId,@Param("productId")Integer productId, @Param("checked")Integer checked);

    int getCartProductCount(@Param("userId")Integer userId);

    List<Cart> selectCheckedCartByUserId(Integer userId);
}