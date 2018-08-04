package com.mmall.common;

public enum ResponseCode {
    //基本类型参数
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    //成员变量
    private final int code;
    private final String desc;

    //构造方法
    ResponseCode(int code,String desc){
        this.code=code;
        this.desc=desc;
    }

    //外抛开发
    public int getCode(){
        return code;
    }
    public String getDesc(){
        return desc;
    }

}
