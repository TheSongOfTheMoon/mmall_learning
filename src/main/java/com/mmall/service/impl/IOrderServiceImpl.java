package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Conts;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.utils.*;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("ALL")
@Service("iOrderService")//为了方便被注入
@Slf4j
public class IOrderServiceImpl implements IOrderService {

//================================springIoC容器与日志参数===========================================================================================//
    //private Logger logger= LoggerFactory.getLogger(ICategoryServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private IUserService iUserService;


//================================逻辑控制层方法===================================================================================================//

    //取消订单
    public ServerResponse<String>  cancelOrder(Integer userId,Long orderNo){
        Order order=orderMapper.selectByUserIdAndOrder(userId,orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("查无该订单或者订单已经取消");
        }
        if (order.getStatus()!=Conts.OrderStatusEnum.NO_PAY.getCode()){
            return ServerResponse.createByErrorMessage("已退款无法取消订单");
        }
        Order updateOrder=new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Conts.OrderStatusEnum.CANNEL.getCode());

        int rowCount=orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (rowCount>0){
            return ServerResponse.createBySuccessMessage("订单取消成功");
        }
        return ServerResponse.createByError();
    }

    //创建订单
    public ServerResponse  CreateOrder(Integer userId,Integer shippingId){

        //购物车在数据库是以一条记录登记一个商品,观察商品归属于何人,最后统一集合成一个购物车
        List<Cart> cartList=cartMapper.selectCartByUserId(userId);

        ServerResponse<List<OrderItem>> serverresponse=this.getCatrOrderItem(userId,cartList);
        //判断
        if (!serverresponse.isSuccess()){
            return serverresponse;
        }
        List<OrderItem>  orderItemList=(List<OrderItem>)serverresponse.getData();
        //生成总价
        BigDecimal payment=this.getOrderTotalPrice(orderItemList);


        //生成订单
        Order order=this.assemibleOrder(userId,shippingId,payment);
        if (order==null){
            return ServerResponse.createByErrorMessage("生成订单失败!");
        }
        //判断购物车
        if (CollectionUtils.isEmpty(orderItemList)){
            return ServerResponse.createByErrorMessage("购物车为空!");
        }

        for (OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }

        //批量插入
        orderItemMapper.BatchInsert(orderItemList);


        //生成成功,减少产品库存，清空购物车
        this.reduceProductStock(orderItemList);

        //购物车
        this.clearCart(cartList);

        //返回订单明细
        OrderVo orderVo=this.assemibleOrderVo(order,orderItemList);
        log.info("创建订单成功");
        return ServerResponse.createBySuccess(orderVo);

    }

    //返回给前端的对象
    private OrderVo assemibleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo=new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPostage(order.getPostage());
        orderVo.setPaymentTypeDesc(Conts.PaymentTypeEnum.DescByCode(order.getPaymentType()).getValue());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Conts.OrderStatusEnum.DescByStatutsCode(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping=shippingMapper.selectByPrimaryKey(order.getShippingId());

        //组装发货地址选项
        if (shipping!=null){
            orderVo.setReceiveName(shipping.getReceiverName());
            orderVo.setShippingVo(assemibleShippingVo(shipping));
        }

        orderVo.setPaymentTime(DateTimeUtils.DateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtils.DateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtils.DateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtils.DateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtils.DateToStr(order.getCloseTime()));
        orderVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix"));

        //组装购物车商品项目
        List<OrderItemVo> orderItemVoList=Lists.newArrayList();
        for (OrderItem orderItem:orderItemList){
            OrderItemVo orderItemVo=assemibleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);

        return orderVo;
    }

    //OrderItem组装
    private OrderItemVo assemibleOrderItemVo(OrderItem orderItem){
        OrderItemVo  orderItemVo=new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateTimeUtils.DateToStr(orderItem.getCreateTime()));

        return orderItemVo;
    }

    //查询产品
    public ServerResponse getOrderCartProduct(Integer userId){
        OrderProductVo orderProductVo=new OrderProductVo();
        List<Cart> cartList=cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse serverresponse=this.getCatrOrderItem(userId,cartList);
        if (!serverresponse.isSuccess()){
            return serverresponse;
        }
        List<OrderItem> orderItemList=(List<OrderItem>)serverresponse.getData();

        List<OrderItemVo> orderItemVoList=Lists.newArrayList();
        BigDecimal payment=new BigDecimal("0");

        for (OrderItem orderItem:orderItemList){
            payment=BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assemibleOrderItemVo(orderItem));
        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix"));
        log.info("查询产品成功");
        return ServerResponse.createBySuccess(orderProductVo);
    }

    //订单详情
    public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo){
        Order order=orderMapper.selectByUserIdAndOrder(userId,orderNo);
        if (order!=null){
            List<OrderItem> orderItemList=orderItemMapper.getByOrderNoByUserId(orderNo,userId);
            OrderVo orderVo=this.assemibleOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        log.info("查询订单成功");
        return ServerResponse.createByErrorMessage("找不到该订单的明细");
    }


    //个人中心
    public ServerResponse<PageInfo> getOrderList(Integer userId, int pageSize, int pageNum){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList=orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList=this.assemibleOrderVo(orderList,userId);
        PageInfo pageResult=new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySuccess(pageResult);
    }





//====================================后台模块======================================================
    //订单分页
    public ServerResponse<PageInfo>  manageList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList=orderMapper.selectAll();
        List<OrderVo>  orderVoList=this.assemibleOrderVo(orderList,null);
        PageInfo pageResult=new PageInfo(orderList);
        pageResult.setList(orderVoList);

        return ServerResponse.createBySuccess(pageResult);
    }

    //订单详情
    public ServerResponse<OrderVo>  manageOrderDetail(Long orderNo){
        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("找不到该订单的详情");
        }
        List<OrderItem> orderItemList=orderItemMapper.getByOrderNoBy(orderNo);
        OrderVo orderVo=this.assemibleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    //订单搜索
    public ServerResponse<PageInfo>  manageOrderSearch(Long orderNo, int pageSize, int pageNum){
        PageHelper.startPage(pageNum,pageSize);
        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("找不到该订单的详情");
        }
        List<OrderItem> orderItemList=orderItemMapper.getByOrderNoBy(orderNo);
        OrderVo orderVo=this.assemibleOrderVo(order,orderItemList);
        PageInfo pageResult=new PageInfo(Lists.newArrayList(order));
        pageResult.setList(Lists.newArrayList(orderVo));

        return ServerResponse.createBySuccess(pageResult);
    }

    //订单发货
    public ServerResponse<String>  manageSendGoods(Long orderNo){
        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("找不到该订单的详情");
        }
        else if(order.getStatus()==Conts.OrderStatusEnum.PAYID.getCode()){
            order.setStatus(Conts.OrderStatusEnum.SHIPPID.getCode());
            order.setSendTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
            return ServerResponse.createBySuccessMessage("发货成功");

        }else{
            return ServerResponse.createByErrorMessage("订单异常");
        }
    }

    //定时关闭订单
    @Override
    public void CloseOrderTask(int hour) {
        log.info("查询超过{} 小时的超时订单",String.valueOf(hour));
        Date closeDateTime= DateUtils.addHours(new Date(),-hour);
        //获取订单
        List<Order> orderList=orderMapper.selectOrderStatusByCreateTime(Conts.OrderStatusEnum.NO_PAY.getCode(),DateTimeUtils.DateToStr(closeDateTime));
        for (Order order:orderList){
            List<OrderItem> orderItemList=orderItemMapper.getByOrderNoBy(order.getOrderNo());
            for (OrderItem orderItem :orderItemList){
                //一定要用主键where作为条件,防止锁表,同时必须支持mysql的InnoDB引擎
                Integer stock=productMapper.selectStockByProductId(orderItem.getProductId());

                if (stock==null){
                    //如果商品被下架了,那么便没必要更新
                    continue;
                }
                log.info("维护超时订单的商品库存");
                //之所以要用新对象，而不用旧的对象，是为了简化SQL
                Product product=new Product();
                product.setId(orderItem.getId());
                product.setStock(stock+orderItem.getQuantity());
                productMapper.updateByPrimaryKeySelective(product);

            }
            log.info("关闭订单OrderByNo:{}",order.getOrderNo());
            orderMapper.closeOrderByOrderId(order.getId());//关闭订单
            log.info("关闭订单OrderByNo:{} 成功",order.getOrderNo());
        }


    }


//===================================VO封装=======================================================================================================//

    //OrderVo封装
    private List<OrderVo> assemibleOrderVo(List<Order> orderList,Integer userId){
        List<OrderVo> orderVoList=Lists.newArrayList();
        for (Order order:orderList){
            List<OrderItem> orderItemList=Lists.newArrayList();
            if (userId==null){
                //管理员查询的时候,不携带UserId的管理员
                orderItemList=orderItemMapper.getByOrderNoBy(order.getOrderNo());
            }else{

                orderItemList=orderItemMapper.getByOrderNoByUserId(order.getOrderNo(),order.getUserId());
            }
            OrderVo orderVo=this.assemibleOrderVo(order,orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    //Vo封装
    private ShippingVo assemibleShippingVo(Shipping shipping){
        ShippingVo shippingVo=new ShippingVo();
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        return shippingVo;
    }


    //清空购物车
    private void clearCart(List<Cart> cartList){
        for (Cart cart:cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    //更新库存
    private void reduceProductStock(List<OrderItem>  orderItemList){
        for (OrderItem orderItem:orderItemList){
            Product product=productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    //创建订单
    private Order assemibleOrder(Integer userId,Integer shippingId,BigDecimal payment){
        Order order=new Order();
        Long orderNo=this.generateOrder();
        order.setOrderNo(orderNo);
        order.setStatus(Conts.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(Conts.PaymentTypeEnum.PAY_ONLINE.getCode());
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);

        int rowCount=orderMapper.insert(order);
        if (rowCount>0){
            return order;
        }
        return null;
    }

    //订单号生成规则
    private  long generateOrder(){
        long currentTime=System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }

    //计算总价
    private BigDecimal getOrderTotalPrice(List<OrderItem>  orderItemList){
            BigDecimal payment=new BigDecimal("0");
            for (OrderItem orderItem:orderItemList){
                payment=BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            }
            return payment;
    }

    //该订单的返回值:每一个子商品
    public ServerResponse<List<OrderItem>>  getCatrOrderItem(Integer userId,List<Cart> cartList){
        List<OrderItem> orderItemList=Lists.newArrayList();
        if (CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        for (Cart cartItem:cartList){
            OrderItem orderItem=new OrderItem();
            Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
            if (Conts.ProductSatusEnum.ON_SALE.getCode()!=product.getStatus()){
                return ServerResponse.createByErrorMessage("产品-->"+product.getName()+"已经下架");
            }

            //校验库存
            if (cartItem.getQuantity()>product.getStock()){
                return ServerResponse.createByErrorMessage("库存不足");
            }

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity()));
            orderItemList.add(orderItem);

        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    //订单号,用户Id,路径
    public ServerResponse pay(Long orderNo,Integer userId,String path){
        log.info("开始支付订单3");
        Map<String,String> resultMap= Maps.newHashMap();
        Order order=orderMapper.selectByUserIdAndOrder(userId,orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("该客户无该订单!");
        }
        resultMap.put("orderNo",String.valueOf(order.getOrderNo()));

        //封装支付宝参数

        String outTradeNo =order.getOrderNo().toString();

        String subject =new StringBuilder().append("文星小筑扫码支付，订单号:").append(outTradeNo).toString();

        // 需保证商户系统端不能重复，建议通过数据库sequence生成，

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body =new StringBuilder().append("订单").append(outTradeNo).append("共购买商品").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";//连锁店

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        //计算每一类商品的价格
        List<OrderItem> orderItemList=orderItemMapper.getByOrderNoByUserId(orderNo,userId);
        for (OrderItem orderItem:orderItemList){
            GoodsDetail goods=GoodsDetail.newInstance(orderItem.getProductId().toString(),orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),orderItem.getQuantity());
            goodsDetailList.add(goods);
        }




        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperties("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();



        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);


                File folder=new File(path);
                if (!folder.exists()){
                    folder.setWritable(true);
                    folder.mkdirs();
                }
                String qrPath = String.format(path+"/qr-%s.png", response.getOutTradeNo());
                String qrFileName=String.format("qr-%s.png",response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);


                File targetFile=new File(path,qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    log.error("上传二维码异常："+e.getMessage());
                    e.printStackTrace();
                }
                // 需要修改为运行机器上的路径
                log.info("二维码文件路径:" + qrPath);
                String qrUrl= PropertiesUtil.getProperties("ftp.server.http.prefix")+targetFile;
                resultMap.put("qrUrl",qrUrl);

                return ServerResponse.createBySuccess(resultMap);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                return  ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return  ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return  ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }


    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    //回调服务
    public ServerResponse alipayCallback(Map<String,String>params){
        Long orderNo=Long.parseLong(params.get("out_trade_no"));
        String tradeNo=params.get("trade_no");
        String tradeStatus=params.get("trade_status");

        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("文星小筑订单不存在,回调忽略");
        }
        if (order.getStatus()> Conts.OrderStatusEnum.PAYID.getCode()){
            return ServerResponse.createBySuccess("支付宝重复回调");
        }
        log.info("来到这里修改状态......");
        if (Conts.AlipayCallBack.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            log.info("来到这里修改状态2......");
            order.setPaymentTime(DateTimeUtils.StrToDate(String.valueOf(params.get("gmt_payment"))));
            order.setStatus(Conts.OrderStatusEnum.PAYID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo=new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Conts.PayPlatfromEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);

        return ServerResponse.createBySuccess();
    }

    public ServerResponse queryOrderByStatus(Integer userId,Long orderNo){
        Order order=orderMapper.selectByUserIdAndOrder(userId,orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("查无该订单");
        }
        return ServerResponse.createByError();
    }


//=============================尾巴============================================================================================================//

}
