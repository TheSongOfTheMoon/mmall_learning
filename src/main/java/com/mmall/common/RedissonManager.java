package com.mmall.common;

import com.mmall.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;
import sun.security.krb5.KrbException;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class RedissonManager {

    /*此处也可以使用lombok,但会造成过多的开发和工具类不完整，一般只在pojo里面生成就可以了*/
    /*Redisson速读:用于管理redisson的各类方法*/
    private Config config=new Config();

    private Redisson redisson=null;

    private static String  redis1Ip= PropertiesUtil.getProperties("redis1.pool.ip");
    private static Integer redis1Port=Integer.parseInt(PropertiesUtil.getProperties("redis1.pool.port","6379"));

    private static String  redis2Ip= PropertiesUtil.getProperties("redis2.pool.ip");
    private static Integer redis2Port=Integer.parseInt(PropertiesUtil.getProperties("redis2.pool.port","6380"));

    /*在构造方法执行后执行*/
    @PostConstruct
    private  void  init(){
        log.info("==========================初始化Redisson==================================");
        try {
            config.useSingleServer().setAddress(new StringBuilder().append(redis1Ip).append(":").append(redis1Port).toString());
            redisson= (Redisson) Redisson.create(config);//加载配置
            log.info("-----redisson初始化成功---");
        } catch (Exception e) {
            log.error("redisson连接redis出错",e);
            e.printStackTrace();
        }
    }

    //单开放redisson
    public Redisson getRedisson() {
        return redisson;
    }

}
