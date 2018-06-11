package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {

    ServerResponse SaveOrUpdateProduct(Product product);

    ServerResponse<String>  SetSaleStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo>  GetManageProductDetails(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse getProductSearchList(String productName,Integer productId,int pageNum,int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductBykeyWord(String keyword,Integer categoryId,Integer pageNum,Integer pageSize,String orderBy);
}
