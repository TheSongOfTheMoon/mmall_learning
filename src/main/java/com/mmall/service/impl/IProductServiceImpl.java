package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Conts;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.utils.DateTimeUtils;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")//方便被Controller注入到
@Slf4j
public class IProductServiceImpl implements IProductService {

    //日志跟踪
    //private Logger logger= LoggerFactory.getLogger(IProductServiceImpl.class);

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse SaveOrUpdateProduct(Product product){
        if (product!=null){

            if (org.apache.commons.lang3.StringUtils.isNotBlank(product.getSubImages())){
                String[] SubImagesArrays=product.getSubImages().split(",");
                if (SubImagesArrays.length>0){
                    product.setMainImage(SubImagesArrays[0]);
                }
            }
            if (product.getId()!=null){
                int resultCount=productMapper.updateByPrimaryKey(product);
                if (resultCount>0){
                    return ServerResponse.createBySuccessMessage("更新品类成功");
                }else{
                    return ServerResponse.createByErrorMessage("更新品类失败");
                }
            }else{
                int resultCount=productMapper.insert(product);
                if (resultCount>0){
                    return ServerResponse.createBySuccessMessage("新增品类成功");
                }else{
                    return ServerResponse.createByErrorMessage("新增品类失败");
                }
            }


        }else{
            return ServerResponse.createByErrorMessage("新增或更新产品参数失败");
        }
    }


    public ServerResponse<String>  SetSaleStatus(Integer productId,Integer status){
        //参数存在
        if (productId==null||status==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
        int resultCount=productMapper.updateByPrimaryKeySelective(product);
        if (resultCount>0){
            return ServerResponse.createBySuccessMessage("修改产品成功");
        }
        return ServerResponse.createByErrorMessage("修长产品销售状态失败");
    }

    public ServerResponse<ProductDetailVo>  GetManageProductDetails(Integer productId){
        //校验参数
        if (productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //查询
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMessage("产品已经下架");
        }
        //搬运到VO中
        ProductDetailVo productDetailVo=setProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);

    }

    //Vo对象
    private ProductDetailVo setProductDetailVo(Product product){
        ProductDetailVo productDetailVo=new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setSubImage(product.getSubImages());
        productDetailVo.setName(product.getName());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setSubitile(product.getSubtitle());
        //imageHost
        productDetailVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category=categoryMapper.selectByPrimaryKey(product.getId());
        if (category==null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        //ParentCategoryId
        productDetailVo.setUpdateTime(DateTimeUtils.DateToStr(product.getUpdateTime()));
        productDetailVo.setCreateTime(DateTimeUtils.DateToStr(product.getCreateTime()));

        return productDetailVo;
    }


    //分页的服务模块
    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){

        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.selectList();

        List<ProductListVo>  productListVoList= Lists.newArrayList();

        //将查询到的对象重新装载,放入Vo的list中
        for(Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //放入分页插件处理
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }


    //分页Vo
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo=new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setPrice(product.getPrice());
        productListVo.setName(product.getName());
        productListVo.setImageHost(product.getMainImage());
        productListVo.setStatus(product.getStatus());
        productListVo.setSubitile(product.getSubtitle());
        productListVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix","http://img.happymmall.com/"));
        return productListVo;
    }



    //后台商品搜索
    public ServerResponse getProductSearchList(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(productName)){
            productName=new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product>  productList=productMapper.selectByProductNameAndId(productName,productId,pageNum,pageSize);

        //将查询到的对象重新装载,放入Vo的list中
        List<ProductListVo>  productListVoList= Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo  pageResult=new PageInfo();
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }



    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        //校验参数
        if (productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //查询
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMessage("产品已经下架");
        }
        if (product.getStatus()!= Conts.ProductSatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已经下架");
        }
        ProductDetailVo productDetailVo=setProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }


    public ServerResponse<PageInfo> getProductBykeyWord(String keyword,Integer categoryId,Integer pageNum,Integer pageSize,String orderBy){
        log.info("进来了");
        if (org.apache.commons.lang3.StringUtils.isBlank(keyword) && categoryId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer>  categoryIdList=new ArrayList<Integer>();
        if (categoryId!=null){
            Category category=categoryMapper.selectByPrimaryKey(categoryId);//产品类型
            if (category==null && org.apache.commons.lang3.StringUtils.isBlank(keyword)){
                //返回一个空的结果集,不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo>  productListVoList=Lists.newArrayList();
                PageInfo pageinfo=new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageinfo);
            }
            categoryIdList=iCategoryService.selectCategoryAndChilerenById(category.getId()).getData();
        }
        log.info("关键字");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(keyword)){
            //单线程可变字符串序列
            keyword=new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        log.info("分页");
        PageHelper.startPage(pageNum,pageSize);

        if (org.apache.commons.lang3.StringUtils.isNotBlank(orderBy)){
            if (Conts.ProductListOderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray=orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);//排序

            }
        }
        //三言运算符
        List<Product> productList=productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
        List<ProductListVo> productListVoList=Lists.newArrayList();
        for (Product productItem :productList){
           ProductListVo productListVo=assembleProductListVo(productItem);
           productListVoList.add(productListVo);
        }

        //分页设置
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);

    }
}
