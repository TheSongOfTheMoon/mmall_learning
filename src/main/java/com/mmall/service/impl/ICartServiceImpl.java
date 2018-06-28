package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Conts;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.utils.BigDecimalUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
@Slf4j
public class ICartServiceImpl implements ICartService {

    //日志跟踪
    //private org.slf4j.Logger logger= LoggerFactory.getLogger(ICartServiceImpl.class);


    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;


    public ServerResponse<CartVo> list(Integer userId){
        if (userId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        CartVo cartVo=this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }



    //添加购物车
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){
        log.info("进入购物车"+String.valueOf(userId)+":"+String.valueOf(productId)+":"+String.valueOf(count));
        if (count==null||productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //根据用户ID和产品ID查找
        Cart cart=cartMapper.selectCartByUserIdandProductId(userId,productId);
        log.info("我在这里。。。。。。。。。");
        if (cart==null){
            //购物车不存在，需要新增
            Cart cartItem=new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Conts.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }else{
            //购物车存在,新增产品
            count=cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);

        }
        CartVo cartVo=this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    //设置购物车
    private CartVo getCartVoLimit(Integer userId){
        //视图对象
        CartVo cartVo=new CartVo();
        //查询是否存在购物车
        List<Cart> cartList=cartMapper.selectCartByUserId(userId);

        List<CartProductVo> cartProductVoList=Lists.newArrayList();

        BigDecimal cartTotalPrice=new BigDecimal("0");//默认值参数

        //如果购物车非空
        if (CollectionUtils.isNotEmpty(cartList)){
            for (Cart cartItem:cartList){
                CartProductVo cartProductVo=new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());
                //初始化总价
                cartProductVo.setProductTotalPrice(new BigDecimal("0"));
                Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product!=null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());//商品状态
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductPrice(product.getPrice());
                    //库存
                    int buylimitCount=0;

                    if (product.getStock()>=cartItem.getQuantity()){
                        cartProductVo.setLimitquantity(Conts.Cart.LIMIT_NUM_SUCCESS);
                        buylimitCount=cartItem.getQuantity();
                    }else{
                        buylimitCount=product.getStock();
                        cartProductVo.setLimitquantity(Conts.Cart.LIMIT_NUM_FAIL);
                        //只会更新有的，所以创建一个新的对象
                        Cart cartForQuantity=new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buylimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    log.info("数量:"+String.valueOf(buylimitCount));
                    cartProductVo.setQuantity(buylimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                if (cartItem.getChecked()==Conts.Cart.CHECKED){
                    //如果已经勾选,增加到整个的购物车总价中
                    log.info(String.valueOf(cartProductVo.getProductTotalPrice()));
                    cartTotalPrice= BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                    log.info("在这里3");
                }
                cartProductVoList.add(cartProductVo);
            }

        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVos(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix"));
        return cartVo;
    }

    private boolean  getAllCheckStatus(Integer userId){
        if (userId==null){
            return false;
        }else{
            return cartMapper.selectCartCheckStatusByUserId(userId)==0;
        }
    }



    //更新购物车
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count){
        if (count==null||productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        }
        //根据用户ID和产品ID查找
        Cart cart=cartMapper.selectCartByUserIdandProductId(userId,productId);
        if (cart!=null){
            cart.setQuantity(count);
        }
        CartVo cartVo=this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }


    //删除产品，前端传过productIds串，以逗号分隔
    public ServerResponse<CartVo> delete(Integer userId,String productIds){
        List<String> productIdList= Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productIdList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productIdList);
        CartVo cartVo=this.getCartVoLimit(userId);
        log.info("删除成功");
        return ServerResponse.createBySuccess(cartVo);
    }


    //全选或者反选
    public ServerResponse<CartVo>  selectAllOrUnselectAll(Integer userId,Integer checked){
        if (userId==null||checked==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
       cartMapper.checkOrUncheckAllProduct(userId,checked);
       return this.list(userId);

    }


    //全选或者反选
    public ServerResponse<CartVo>  selectAllOrUnselect(Integer userId,Integer productId,Integer checked){
        if (userId==null||checked==null||productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.checkOrUncheckProduct(userId,productId,checked);
        return this.list(userId);

    }

    //查找购物车全部数量
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if (userId==null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.getCartProductCount(userId));
    }



}
