package com.mmall.test;

import com.mmall.utils.DateTimeUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

public class BigDecimalTest {

    @Test
    public void Test1(){
        System.out.println(0.05+0.01);
        System.out.println(1.0-0.42);
        System.out.println(5.015*100);
        System.out.println(123.3/100);
    }

    @Test
    public void Test2(){
        BigDecimal bd=new BigDecimal("0.01");
        BigDecimal bd2=new BigDecimal("0.05");
        System.out.println(bd.add(bd2));
    }

    //非String构造器用于科研
    @Test
    public void Test3(){
        BigDecimal bd=new BigDecimal(0.01);
        BigDecimal bd2=new BigDecimal(0.05);
        System.out.println(bd.add(bd2));
    }


    @Test
    public void Tset4(){
        String trade_status="2018-06-17 20:58:17";
        Date yyyy=DateTimeUtils.StrToDate(trade_status);
        System.out.println(yyyy);
    }
}
