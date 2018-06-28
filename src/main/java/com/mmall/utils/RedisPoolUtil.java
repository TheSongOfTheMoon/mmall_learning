package com.mmall.utils;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
public class RedisPoolUtil {



    /*设置有效期多久*/
    public static Long setExpireJedis(String key,int extime){
        log.info("重置键值"+key+"的有效期为:"+String.valueOf(extime));
        Jedis jedis=null;
        Long jedisResult=null;
        try{
            jedis= RedisPool.getJedis();
            jedisResult=jedis.expire(key,extime);
        }catch(Exception e){
            log.error("set key:{} value:{} error",key,extime,e);//不用getMessage是因为信息太少了
            RedisPool.returnBrokenResource(jedis);
            return jedisResult;
        }
        RedisPool.returnJedis(jedis);
        return jedisResult;
    }



    /*设置key-value值*/
    public static String setJedis(String key,String value){
        Jedis jedis=null;
        String jedisResult=null;
        try{
            jedis= RedisPool.getJedis();
            jedisResult=jedis.set(key,value);
        }catch(Exception e){
            log.error("set key:{} value:{} error",key,value,e);//不用getMessage是因为信息太少了
            RedisPool.returnBrokenResource(jedis);
            RedisPool.returnBrokenResource(jedis);
            return jedisResult;
        }
        RedisPool.returnJedis(jedis);
        return jedisResult;
    }


    /*获取value*/
    public static String getJedis(String key){
        Jedis jedis=null;
        String jedisResult=null;
        try{
            jedis= RedisPool.getJedis();
            jedisResult=jedis.get(key);
        }catch(Exception e){
            log.error("get key:{} value error",key,e);//不用getMessage是因为信息太少了
            RedisPool.returnBrokenResource(jedis);
            return jedisResult;
        }
        RedisPool.returnJedis(jedis);
        return jedisResult;
    }

    /*有效时间，extime 单位是秒*/
    public static String setExJedis(String key,int extime,String value){
        log.info("设置键值"+key+"的有效期为:"+String.valueOf(extime));
        Jedis jedis=null;
        String jedisResult=null;
        try{
            jedis= RedisPool.getJedis();
            jedisResult=jedis.setex(key,extime,value);
        }catch(Exception e){
            log.error("set key:{} EXtime：{} value:{} error",key,extime,value,e);//不用getMessage是因为信息太少了
            RedisPool.returnBrokenResource(jedis);
            return jedisResult;
        }
        RedisPool.returnJedis(jedis);
        return jedisResult;
    }


    /*删除value*/
    public static Long delJedis(String key){
        Jedis jedis=null;
        Long jedisResult=null;
        try{
            jedis= RedisPool.getJedis();
            jedisResult=jedis.del(key);
        }catch(Exception e){
            log.error("get key:{} value error",key,e);//不用getMessage是因为信息太少了
            RedisPool.returnBrokenResource(jedis);
            return jedisResult;
        }
        RedisPool.returnJedis(jedis);
        return jedisResult;
    }

    /*调试测试方法*/
    public static void main(String[] args){
        Jedis jedis=RedisPool.getJedis();
        RedisPoolUtil.setJedis("Q_Q","YYY");
        String value=RedisPoolUtil.getJedis("Q_Q");
        System.out.println("打印:"+value);

        RedisPoolUtil.setExJedis("Q_Q",60*1,value+"111");
        RedisPoolUtil.setExpireJedis("Q_Q",60*2);
        System.out.println("结束了");
    }

}
