package com.mmall.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {

    private BigDecimalUtil(){
    }

    public static BigDecimal add(double v1,double v2){
        BigDecimal bd=new BigDecimal(v1);
        BigDecimal bd2=new BigDecimal(v2);
        return bd.add(bd2);
    }

    public static BigDecimal sub(double v1,double v2){
        BigDecimal bd=new BigDecimal(v1);
        BigDecimal bd2=new BigDecimal(v2);
        return bd.subtract(bd2);
    }

    public static BigDecimal mul(double v1,double v2){
        BigDecimal bd=new BigDecimal(v1);
        BigDecimal bd2=new BigDecimal(v2);
        return bd.multiply(bd2);
    }

    public static BigDecimal div(double v1,double v2){
        BigDecimal bd=new BigDecimal(v1);
        BigDecimal bd2=new BigDecimal(v2);
        return bd.divide(bd2,2,BigDecimal.ROUND_HALF_UP);//保留两位小数,四舍五入
    }
}
