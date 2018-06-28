package com.mmall.utils;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeUtils {

    private static Logger  logger= LoggerFactory.getLogger(DateTimeUtils.class);

    public static final  String  STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";

    public static Date StrToDate(String strTime,String dataFormer){
        DateTimeFormatter dateTimeFormatter=DateTimeFormat.forPattern(strTime);
        DateTime dateTime=dateTimeFormatter.parseDateTime(dataFormer);
        return dateTime.toDate();
    }

    public static String DateToStr(Date time,String strFormer){
        if (time==null){
            return StringUtils.EMPTY;
        }

        DateTime datetime=new DateTime(time);
        return datetime.toString();

    }

//--------------------------------------------------------------------------//
    public static Date StrToDate(String strTime){
        logger.info("打印时间:"+strTime);
        DateTimeFormatter dtf=DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime=dtf.parseDateTime(strTime);
        logger.info("打印时间3:"+dateTime.toDate());
        return dateTime.toDate();
    }

    public static String DateToStr(Date time){
        if (time==null){
            return StringUtils.EMPTY;
        }

        DateTime datetime=new DateTime(time);
        return datetime.toString(STANDARD_FORMAT);

    }


}
