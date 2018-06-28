package com.mmall.controller.portal;


import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product/")
@Slf4j
public class ProductController {

    @Autowired
    private IProductService iProductService;

    //获取商品详情
    @RequestMapping(value ="getDetail.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo>  getDetail(Integer productId){
           return iProductService.getProductDetail(productId);
    }

    @RequestMapping(value ="getProduuctList.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> getProduuctList(@RequestParam(value ="keyword",required =false)String keyword,
                                                    @RequestParam(value ="categoryId",required =false)Integer categoryId,
                                                    @RequestParam(value="pageNum",defaultValue ="1")Integer pageNum,
                                                    @RequestParam(value="pageSize",defaultValue ="10")Integer pageSize,@RequestParam(value="orderBy",defaultValue ="")String orderBy){
        return iProductService.getProductBykeyWord(keyword,categoryId,pageNum,pageSize,orderBy);

    }


}
