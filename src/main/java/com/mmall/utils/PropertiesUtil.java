package com.mmall.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {

    //日志
    private static Logger logger= LoggerFactory.getLogger(PropertiesUtil.class);
    private static Properties props;
    static{
        String fileName="mmall.properties";
        props=new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            logger.error("配置文件异常",e);
        }

    }

    //开放方法
    public static String getProperties(String key){
        logger.info("读取配置文件信息");
        if (org.apache.commons.lang3.StringUtils.isBlank(key)){
            return "属性值为空";
        }
        String value=props.getProperty(key.trim());
        if (org.apache.commons.lang3.StringUtils.isBlank(value)){

            return  "配置文件为空";
        }else{
            return value.trim();
        }
    }

    //重载数据:如果传过来找不到就把默认值回传
    public static String getProperties(String key,String defaultValue){
        logger.info("读取  "+String.valueOf(key)+"   配置文件信息,读不到则使用默认值");
        String value=props.getProperty(key.trim());
        if (org.apache.commons.lang3.StringUtils.isBlank(value)){
            logger.info("使用  "+String.valueOf(key)+"  默认值");
            return  defaultValue;
        }else{
            logger.info("读取到  "+String.valueOf(key)+" 配置值");
            return value.trim();
        }
    }
}
