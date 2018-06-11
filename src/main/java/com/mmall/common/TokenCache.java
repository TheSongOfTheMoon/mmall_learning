package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class TokenCache {
    public static Logger logger= LoggerFactory.getLogger(TokenCache.class);

    //使用LRU算法来实现默认执行日志加载,指定缓存区大小和刷新算法
    private static LoadingCache<String,String> localCache= CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
        //本处使用默认加载,当get的key不存在,那么则会使用该方法来加载数据
        @Override
        public String load(String key) throws Exception {
            return "null";
        }
    });

    //开放方法
    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    //开放方法
    public  static String getKey(String key){
        String value= null;
        try {
            value = localCache.get(key);
            if ("null".equals(value)){
                return null;
            }
            return value;

        } catch (ExecutionException e) {
            e.printStackTrace();
            logger.error("localCache  get   error",e.getMessage());
        }
        return null;
    }




}
