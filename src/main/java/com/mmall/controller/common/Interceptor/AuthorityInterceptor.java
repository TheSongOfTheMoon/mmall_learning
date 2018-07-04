package com.mmall.controller.common.Interceptor;

import com.google.common.collect.Maps;
import com.mmall.common.Conts;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JacksonUtil;
import com.mmall.utils.RedisShardedJedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        log.info("preHandle拦截-未登录和无权限的操作");
        log.info("===================获取Controller中的方法名==========================");
        HandlerMethod handlerMethod=(HandlerMethod)handler;
        log.info("获取");
        String methodName=handlerMethod.getMethod().getName();//方法名
        String className=handlerMethod.getBean().getClass().getSimpleName();//类名

        log.info("解析");
        StringBuffer stringBuffer=new StringBuffer();//线程安全(慢)
        Map paramsMap=httpServletRequest.getParameterMap();
        Iterator it=paramsMap.entrySet().iterator();//enrtySet可以就是key-value对
        while(it.hasNext()){
            Map.Entry entry=(Map.Entry)it.next();
            String mapKey= (String) entry.getKey();

            String mapValue= StringUtils.EMPTY;
            //request里面的map,返回的是一个String[]
            Object obj=entry.getValue();
            if (obj instanceof String[]){
                String[] strs= (String[]) obj;
                mapValue= Arrays.toString(strs);//java库中数组的方法
            }
            stringBuffer.append(mapKey).append("=").append(mapValue).append("; ");
        }

        /*用户登录拦截忽略*/
        if (StringUtils.equals(className,"UserManagerController")&&StringUtils.equals(methodName,"loginAdmin")){
            //如果拦截到登录，不执行拦截
            log.info("拦截器拦截到请求 Controller：{},Method:{},程序放行",className,methodName);
            return true;
        }


        log.info("日志信息:"+stringBuffer.toString());
        User user=null;
        //拦截器处理

        log.info("获取Token判断权限");
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isNotEmpty(loginToken)){
            String strLoginToken= RedisShardedJedisPoolUtil.getJedis(loginToken);//将用户登录信息存入redis中
            user= JacksonUtil.StrToObject(strLoginToken,User.class);
        }

        if (user==null || (user.getRole().intValue()!= Conts.Role.ROLE_ADMIN)){

            log.info("重置返回容器");
            //用户为空或者用户不为管理员,不允许调用controller方法
            httpServletResponse.reset();//此处需要进行重置，否则会报已经存在的异常
            httpServletResponse.setCharacterEncoding("UTF-8");//脱离了springMVC的监控，需要重新设置编码(正常在dispatcherServlet也有设置)
            httpServletResponse.setContentType("application/json;charset=UTF-8");//设置返回值类型

            //写
            PrintWriter printWriter=httpServletResponse.getWriter();
            if (user==null){

                //拦截富文本上传
                if (StringUtils.equals(className,"ProductManageController")&&StringUtils.equals(methodName,"richtext_img_upload")){
                    Map resultMap= Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "请登录管理员");
                   printWriter.print(JacksonUtil.objToString(resultMap));
                }else{
                    printWriter.println(JacksonUtil.objToString(ServerResponse.createByErrorMessage("拦截器拦截：用户未登录")));
                }
            }else{
                if (StringUtils.equals(className,"ProductManageController")&&StringUtils.equals(methodName,"richtext_upload")){
                    Map resultMap= Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无权限操作");
                    printWriter.print(JacksonUtil.objToString(resultMap));
                }else {
                    printWriter.println(JacksonUtil.objToString(ServerResponse.createByErrorMessage("拦截器拦截：用户无权限")));
                }
            }
            printWriter.flush();//写入
            printWriter.close();//关闭

            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");
    }
}
