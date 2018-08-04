package com.Test;

import com.google.common.collect.Lists;
import com.mmall.utils.BigDecimalUtil;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

public class Test {

    public int tb_num=0;
    public  ArrayList<Integer> tb_price= new ArrayList<Integer>();//测试的代码
    public  static BigDecimal stand_price=new BigDecimal("0");
    public static Integer tbNum=0;


    //统计总价
    public double Count(){
        double  totalprice=0;

        Iterator<Integer> List1=tb_price.iterator();
        while (List1.hasNext()){
            Integer price=List1.next();
            if (price!=null){
                if (price>=BigDecimalUtil.mul(31300000,0.88).doubleValue()){
                    tbNum=tbNum+1;
                }
                stand_price= BigDecimalUtil.add(stand_price.doubleValue(),price.doubleValue());
            }
        }
        totalprice=stand_price.doubleValue();
        return totalprice;
    }


    //计算差价




    //计算总价----计算基准价
    public  BigDecimal doCount(ArrayList<Integer> arrayList1){
        BigDecimal tb_price=new BigDecimal(0);
        //大于6家

        //差价
        double MaxAndmin=BigDecimalUtil.add(Calculation.CalculationMax(arrayList1),Calculation.CalculationMin(arrayList1)).doubleValue();


        double tataolprice=Count();//总价
        //>大于

        //有效投标数
        if (tbNum>=6){
            //计算总价减去最低价和最高价
            double allPrice=BigDecimalUtil.mul(0.98,BigDecimalUtil.sub(tataolprice,MaxAndmin).doubleValue()).doubleValue();
            tb_price=BigDecimalUtil.div(allPrice,(tbNum-2));
        }else{
            tb_price=BigDecimalUtil.mul(0.98,BigDecimalUtil.mul(tataolprice,BigDecimalUtil.div(arrayList1.size(),tb_num).doubleValue()).doubleValue());
            //投标价有效，但是数量无效
        }
        return tb_price;

    }



    //计算得分
    public  void getCount(ArrayList<Integer> list2,BigDecimal tbPn){
            Iterator<Integer> it=list2.iterator();
            while(it.hasNext()){

                Integer it2=it.next();
                double sss=BigDecimalUtil.sub(it2.doubleValue(),tbPn.doubleValue()).doubleValue();
                if (sss>=0){

                }

            }
    }

}
