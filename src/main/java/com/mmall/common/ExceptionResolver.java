package com.mmall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@Component/*组件注解@Reposity用在dao上,service用在server上，非dao、service都用compont组件,效果都是成为spring的bean*/
public  class ExceptionResolver implements HandlerExceptionResolver{

    /*解决异常*/
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        log.error("{} Exception ",httpServletRequest.getRequestURI(),e);
        //MappingJacksonJsonView是view的实现类,所以可以被使用
        ModelAndView modelAndView=new ModelAndView(new MappingJacksonJsonView());

        //我们需要模拟controller放回jackson给前端的,所以根据项目中jackson中的版本选择使用MappingJacksonJsonView，如果jackson2.x则使用MappingJackson2JsonView
        modelAndView.addObject("status",ResponseCode.ERROR.getCode());
        modelAndView.addObject("msg","网关通讯异常，详情请查看服务端日志的异常信息");
        modelAndView.addObject("data",e.toString());

        return modelAndView;
    }


}
