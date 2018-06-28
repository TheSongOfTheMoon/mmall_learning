package com.mmall.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtil {

    private final static  String COOKIE_DOMAIN=".cyqxm.com";
    private final static  String COOKIE_NAME="mmall_login_token";

    //写入Cookie
    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie ck=new Cookie(COOKIE_NAME,token);
        ck.setDomain(COOKIE_DOMAIN);
        ck.setPath("/");//代表放置在根目录，如果放在指定目录，则只有指定目录可以获取到cookie
        ck.setHttpOnly(true);//保护站点被脚本攻击 tomcat>=7

        /*
        * 以秒为单位
        * -1代表永久
        *如果不设置不会报错,但是只会保存在内存中，不会写入硬盘，有效期为当前页面
        * 当前设置为一年
        * */
        ck.setMaxAge(60*60*24*365);

        log.info("打印Cookie的信息，Cookie名称:{},Cookie键值:{}",ck.getName(),ck.getValue());

        response.addCookie(ck);
        log.info("写入Cookie的信息完毕");
    }


    //读取Cookie
    public static String readLoginToken(HttpServletRequest request){
        Cookie[]  cks=request.getCookies();
        if (cks!=null){
            for (Cookie ck:cks){
                log.info("read CookieName:{},CookieValue:{}",ck.getName(),ck.getValue());
                if (StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    log.info("return CookieName:{},CookieValue:{}",ck.getName(),ck.getValue());
                    return ck.getValue();
                }
            }
        }
        log.info("读取不到满足条件的Cookie");
        return null;

    }

    //删除Cookie
    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[]  cks=request.getCookies();
        if (cks!=null){
            for (Cookie ck:cks){
                log.info("read CookieName:{},CookieValue:{}",ck.getName(),ck.getValue());
                if (StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0);//设置成0表示删除Cookie
                    log.info("del CookieName:{},CookieValue:{}",ck.getName(),ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }
        }
        log.info("找不到想删除的Cookie");
    }

}
