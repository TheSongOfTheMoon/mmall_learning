package com.mmall.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtils {

    //日志
    private static Logger logger= LoggerFactory.getLogger(PropertiesUtils.class);

    private static Properties props;

    static{
        String fileName="mmall.properties";
        props=new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtils.class.getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            logger.error("配置文件异常",e);
        }

    }

    //开放方法
    public static String getProperties(String key){
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
        String value=props.getProperty(key.trim());
        if (org.apache.commons.lang3.StringUtils.isBlank(value)){

            return  defaultValue;
        }else{
            return value.trim();
        }
    }
}
