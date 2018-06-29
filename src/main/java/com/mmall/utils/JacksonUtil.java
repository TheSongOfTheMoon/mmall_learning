package com.mmall.utils;

import com.google.common.collect.Lists;
import com.mmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.javassist.expr.Instanceof;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
public class JacksonUtil {


    private static ObjectMapper objectMapper=new ObjectMapper();

    static{
        //将对象的所有字段全部列入
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);//序列化行为
        //取消默认转换timestamp模式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);

        //忽略空bean的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

        //设置统一的日期格式
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtils.STANDARD_FORMAT));

        /*反序列化配置*/

        //忽略在jskon中存在,但是在java对象中不存在的属性时的错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);

    }

    /*把对象转化成jskon*/
    public static <T> String objToString(T obj){
        log.error("将传递过来的对象转换为字符串");
        if (obj==null){
            log.error("赋值过来想要转换成jskon的对象为空:"+obj);
            return null;
        }
        try {
            return obj instanceof String ? (String) obj: objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("判断是否字符串类型并转化jskon对象为字符串异常:",e);
            e.printStackTrace();
            return null;
        }
    }

    /**/
     /*把对象转化成格式化的jskon,比较规范*/
    public static <T> String objToStringPretty(T obj){
        log.info("格式化的对象转化");
        if (obj==null){
            log.error("赋值过来想要转换成jskon的对象为空:"+obj);
            return null;
        }
        try {
            return obj instanceof String ? (String) obj: objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("判断是否字符串类型并转化jskon对象为字符串异常:",e);
            e.printStackTrace();
            return null;
        }
    }

    /*转化字符串为jackson*/
    public static <T> T StrToObject(String str,Class<T> clazz){
        if (StringUtils.isEmpty(str)||clazz==null){
            log.error("赋值过来的字符串或对象为空，str：{} class:{}",str,String.valueOf(clazz));
            return  null;
        }
        try {
            return clazz.equals(String.class)? (T)str:objectMapper.readValue(str,clazz);
        } catch (IOException e) {
            log.warn("判断是否字符串类型并转化字符串为jskon对象异常:",e);
            e.printStackTrace();
            return null;
        }
    }





    /*转化字符串为jackson*/
    public static <T> T StrToObjectList(String str,TypeReference<T> typeReference){
        if (StringUtils.isEmpty(str)||typeReference==null){
            log.error("赋值过来的字符串或对象为空，str：{} class:{}",str,String.valueOf(typeReference));
            return  null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)? str:objectMapper.readValue(str,typeReference));
        } catch (IOException e) {
            log.warn("判断是否字符串类型并转化字符串为jskon_List对象异常:",e);
            e.printStackTrace();
            return null;
        }
    }


    /*泛型：反序列化
    *类型<T>：以为着返回的类型是是一个泛型类型的默认值,T意味着代表任意某个类型
    * class<?> :用问号是因为需要回传的类型不知道是什么类型(并不一定是返回的类型),如果写T意味着返回的类型也是一致
    *传入需要转化成集合的类型,传入集合里的类型
    * */
    public static <T> T StrToObjectList(String str,Class<?> CollectionsClass,Class<?> ...elememtClass){
        JavaType javaType=objectMapper.getTypeFactory().constructParametricType(CollectionsClass,elememtClass);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            log.warn("泛型反序列化:",e);
            e.printStackTrace();
            return null;
        }
    }




    public static void main(String[] args){
        User u1=new User();
        u1.setId(1);
        u1.setEmail("wenxin@qq.com");

        //====

        User u2=new User();
        u2.setId(2);
        u2.setEmail("wenxin2@qq.com");


        String jackson=JacksonUtil.objToString(u1);
        String jcaksonPretty=JacksonUtil.objToStringPretty(u1);

        log.info(jackson);
        log.info(jcaksonPretty);

        User user=JacksonUtil.StrToObject(jackson,User.class);
        /*ctrl+U可以打开细节*/

        List<User>  userList= Lists.newArrayList();
        userList.add(u1);
        userList.add(u2);

        String strUserList=JacksonUtil.objToString(userList);
        log.info(strUserList);

        List<User> userList1=JacksonUtil.StrToObjectList(strUserList,new TypeReference<List<User>>(){/*空实现*/});

        List<User>  userList2=JacksonUtil.StrToObjectList(strUserList,List.class,User.class);

        log.info(userList2.toString());

        log.info("用户信息");

    }


}
