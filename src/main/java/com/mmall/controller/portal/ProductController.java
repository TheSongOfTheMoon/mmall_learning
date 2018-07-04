package com.mmall.controller.portal;


import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value ="getProductList.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> getProduuctList(@RequestParam(value ="keyword",required =false)String keyword,
                                                    @RequestParam(value ="categoryId",required =false)Integer categoryId,
                                                    @RequestParam(value="pageNum",defaultValue ="1")Integer pageNum,
                                                    @RequestParam(value="pageSize",defaultValue ="10")Integer pageSize,@RequestParam(value="orderBy",defaultValue ="")String orderBy){
        return iProductService.getProductBykeyWord(keyword,categoryId,pageNum,pageSize,orderBy);

    }


    //获取商品详情
    /*改造成restful一定要带斜杠,然后dispatcherde的拦截http请求也要控制好
    * 对于特别长得路径要记得维护好，或者干脆不使用restful
    * */
    @RequestMapping(value ="/{productId}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo>  getDetailRestFul(@PathVariable Integer productId){
        return iProductService.getProductDetail(productId);
    }
    //http://localhost:8080/product/手机/100012/1/10/price_asc
    @RequestMapping(value ="/{keyword}/{categoryId}/{pageNum}/{pageSize}/{orderBy}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> getProduuctListRestFul(@PathVariable(value ="keyword")String keyword,
                                                    @PathVariable(value ="categoryId")Integer categoryId,
                                                    @PathVariable(value="pageNum")Integer pageNum,
                                                    @PathVariable(value="pageSize")Integer pageSize,
                                                           @PathVariable(value="orderBy")String orderBy){
        if (pageNum==null){
            pageNum=1;
        }
        if (pageSize==null){
            pageSize=10;
        }
        if (StringUtils.isBlank(orderBy)){
            orderBy="price_asc";
        }

        return iProductService.getProductBykeyWord(keyword,categoryId,pageNum,pageSize,orderBy);

    }


    /*使用常量来进行资源占位符*/
    //http://localhost:8080/product/keyword/手机/100012/1/10/price_asc
    @RequestMapping(value ="/keyword/{keyword}/{pageNum}/{pageSize}/{orderBy}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> getProduuctListRestFul1(@PathVariable(value ="keyword")String keyword,
                                                            @PathVariable(value="pageNum")Integer pageNum,
                                                            @PathVariable(value="pageSize")Integer pageSize,
                                                            @PathVariable(value="orderBy")String orderBy){
        if (pageNum==null){
            pageNum=1;
        }
        if (pageSize==null){
            pageSize=10;
        }
        if (StringUtils.isBlank(orderBy)){
            orderBy="price_asc";
        }

        return iProductService.getProductBykeyWord(keyword,null,pageNum,pageSize,orderBy);

    }


    //http://localhost:8080/product/keyword/手机/100012/1/10/price_asc
    @RequestMapping(value ="/categoryId/{categoryId}/{pageNum}/{pageSize}/{orderBy}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> getProduuctListRestFul2(@PathVariable(value ="categoryId")Integer categoryId,
                                                            @PathVariable(value="pageNum")Integer pageNum,
                                                            @PathVariable(value="pageSize")Integer pageSize,
                                                            @PathVariable(value="orderBy")String orderBy){
        if (pageNum==null){
            pageNum=1;
        }
        if (pageSize==null){
            pageSize=10;
        }
        if (StringUtils.isBlank(orderBy)){
            orderBy="price_asc";
        }

        return iProductService.getProductBykeyWord(null,categoryId,pageNum,pageSize,orderBy);

    }

}


