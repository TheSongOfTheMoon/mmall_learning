package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Conts {

    public static final String  CURRENT_USER="curentUser";

    public static final String EMAIL="email";

    public static final String USERNAME="username";
    //常量类里的接口,用来分类,默认为public static final
    public interface Role{
        int ROLE_CUSTOMER=0;//用户
        int ROLE_ADMIN=1;//管理员
    }

    //枚举
    public enum ProductSatusEnum{
        ON_SALE(1,"在售");
        private String value;
        private int code;

        ProductSatusEnum(int code,String value){
                this.code=code;
                this.value=value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }


    public interface ProductListOderBy{//时间复杂度01
        Set<String>  PRICE_ASC_DESC= Sets.newHashSet("price_asc","price_desc");
    }

    public interface RedisCacheExtime{
        int REDIS_SESSION_EXTIME=60*30;//设置登录时间为30分钟
    }


    public interface REDIS_LOCK{
        String TASK_CLOSE_ORDER_LOCK="CLOSE_ORDER_LOCK";//设置锁的key
    }


    public enum OrderStatusEnum{
        CANNEL(0,"已取消"),

        NO_PAY(10,"已取消"),

        PAYID(20,"已支付"),

        SHIPPID(40,"已经发货"),

        ORDER_SUCCESS(50,"订单完成"),

        ORDER_CLOSE(60,"订单关闭");

        OrderStatusEnum(int code,String value){
            this.code=code;
            this.value=value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }
        public int getCode() {
            return code;
        }


        public  static OrderStatusEnum  DescByStatutsCode(int code){
            for (OrderStatusEnum orderStatusEnum:values()){
                if (orderStatusEnum.getCode()==code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("找不到该订单状态");

        }

    }



    public enum PayPlatfromEnum{
        ALIPAY(1,"支付宝");

        PayPlatfromEnum(int code,String value){
            this.code=code;
            this.value=value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }
        public int getCode() {
            return code;
        }
    }




    public interface Cart{
        int CHECKED=1;//购物车选中
        int UNCHECK=0;//购物车未选中状态
        String LIMIT_NUM_FAIL="LIMIT_NUM_FAIL";//失败
        String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";//成功
    }


    public interface AlipayCallBack{
        String TRADE_STATUS_WAIT_BUYER_PAY="WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS="TRADE_SUCCESS";

        String RESPONSE_SUCCESS="success";
        String RESPONSE_FAILED="failed";

    }



    public enum PaymentTypeEnum{
        PAY_ONLINE(1,"在线支付");

        PaymentTypeEnum(int code,String value){
            this.code=code;
            this.value=value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }
        public int getCode() {
            return code;
        }


        public static PaymentTypeEnum  DescByCode(int code){
            for (PaymentTypeEnum paymentTypeEnum:values()){
                if (paymentTypeEnum.getCode()==code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到当前的支付类型");
        }

    }
}
