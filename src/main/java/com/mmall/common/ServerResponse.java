package com.mmall.common;

import com.mmall.pojo.User;
import net.sf.jsqlparser.schema.Server;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.xml.ws.Response;
import java.io.Serializable;
//使用泛型来实现通用
/*此类中,所有不开放的方法和变量是不会显示在json中的,但是一旦被开发就会显示到json中,需要通过jsonIngnore处理*/
/*如果key为null,则不返回*/
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)//途中被开放的三个变量是默认一定返回，有时会出现返回data的空节点,因此需要处理
public class ServerResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;


    private ServerResponse(int status){
        this.status=status;
    }

    private ServerResponse(int status,String msg){
        this.status=status;
        this.msg=msg;
    }

    private ServerResponse(int status,T data){
        this.status=status;
        this.data=data;
    }

    private ServerResponse(int status,String msg,T data){
        this.status=status;
        this.data=data;
        this.msg=msg;
    }
    //判断是否成功：此处使用枚举将合法常量(携带特殊意义)的字符进行声明
    @JsonIgnore
    //使之不在json序列化之中
    public boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS.getCode();
    }

    public int getStatus(){
        return status;
    }
    public String getMsg(){
        return msg;
    }

    public T getData(){
        return data;
    }

    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String msg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),msg);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int eerrorcode,String errormsg){
        return new ServerResponse<T>(eerrorcode,errormsg);
    }


}
