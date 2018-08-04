package com.mmall.utils;

import com.mmall.common.RedisPool;
import com.mmall.common.RedisShardedJedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

@Slf4j
public class RedisShardedJedisPoolUtil {



    /*设置有效期多久(单位秒)*/
    public static Long setExpireJedis(String key,int extime){
        log.info("重置键值"+key+"的有效期为:"+String.valueOf(extime));
        ShardedJedis shardedJedis=null;
        Long shardedJedisResult=null;
        try{
            shardedJedis= RedisShardedJedisPool.getShardedJedis();
            shardedJedisResult=shardedJedis.expire(key,extime);
        }catch(Exception e){
            log.error("set key:{} value:{} error",key,extime,e);//不用getMessage是因为信息太少了
            RedisShardedJedisPool.returnBrokenResource(shardedJedis);
            return null;
        }
        RedisShardedJedisPool.returnShardedJedis(shardedJedis);
        return shardedJedisResult;
    }



    /*设置key-value值*/
    public static String setJedis(String key,String value){
        ShardedJedis shardedJedis=null;
        String shardedJedisResult=null;
        try{
            shardedJedis= RedisShardedJedisPool.getShardedJedis();
            shardedJedisResult=shardedJedis.set(key,value);
        }catch(Exception e){
            log.error("set key:{} value:{} error",key,value,e);//不用getMessage是因为信息太少了
            RedisShardedJedisPool.returnBrokenResource(shardedJedis);
            return null;
        }
        RedisShardedJedisPool.returnShardedJedis(shardedJedis);
        return shardedJedisResult;
    }


    /*获取value*/
    public static String getJedis(String key){
        ShardedJedis shardedJedis=null;
        String shardedJedisResult=null;
        try{
            shardedJedis= RedisShardedJedisPool.getShardedJedis();
            shardedJedisResult=shardedJedis.get(key);
            log.info(shardedJedisResult);
        }catch(Exception e){
            log.error("get key:{} value error",key,e);//不用getMessage是因为信息太少了
            RedisShardedJedisPool.returnBrokenResource(shardedJedis);
            return null;
        }
        RedisShardedJedisPool.returnShardedJedis(shardedJedis);
        return shardedJedisResult;
    }

    /*有效时间，extime 单位是秒*/
    public static String setExJedis(String key,int extime,String value){
        log.info("设置键值"+key+"的有效期为:"+String.valueOf(extime));
        ShardedJedis shardedJedis=null;
        String shardedJedisResult=null;
        try{
            shardedJedis= RedisShardedJedisPool.getShardedJedis();
            shardedJedisResult=shardedJedis.setex(key,extime,value);
        }catch(Exception e){
            log.error("set key:{} EXtime：{} value:{} error",key,extime,value,e);//不用getMessage是因为信息太少了
            RedisShardedJedisPool.returnBrokenResource(shardedJedis);
            return null;
        }finally{
            RedisShardedJedisPool.returnShardedJedis(shardedJedis);
        }
        return shardedJedisResult;
    }


    /*删除value*/
    public static Long delJedis(String key){
        ShardedJedis shardedJedis=null;
        Long shardedJedisResult=null;
        try{
            shardedJedis= RedisShardedJedisPool.getShardedJedis();
            shardedJedisResult=shardedJedis.del(key);
        }catch(Exception e){
            log.error("get key:{} value error",key,e);//不用getMessage是因为信息太少了
            RedisShardedJedisPool.returnBrokenResource(shardedJedis);
            return shardedJedisResult;
        }
        RedisShardedJedisPool.returnShardedJedis(shardedJedis);
        return shardedJedisResult;
    }



    /*只有不存在才可以设置成功*/
    public static Long setNxJedis(String key,String value){
        log.info("分布式锁---设置键值:{}"+String.valueOf(key));
        ShardedJedis shardedJedis=null;
        Long shardedJedisResult=null;
        try{
            shardedJedis= RedisShardedJedisPool.getShardedJedis();
            shardedJedisResult=shardedJedis.setnx(key,value);
        }catch(Exception e){
            log.error("setnx key:{}  value:{} error",key,value,e);//不用getMessage是因为信息太少了
            RedisShardedJedisPool.returnBrokenResource(shardedJedis);
            return null;
        }finally{
            RedisShardedJedisPool.returnShardedJedis(shardedJedis);
        }
        return shardedJedisResult;
    }

    /*getSet方法
    * 返回给定 key 的旧值。 当 key 没有旧值时，即 key 不存在时，返回 nil ,并设置新的值(value)
    * */
    public static String GetOldValNorSetValJedis(String key,String value){
        log.info("分布式锁---设置键值:{}"+String.valueOf(key));
        ShardedJedis shardedJedis=null;
        String shardedJedisResult=null;
        try{
            shardedJedis= RedisShardedJedisPool.getShardedJedis();
            shardedJedisResult=shardedJedis.getSet(key,value);
        }catch(Exception e){
            log.error("setnx key:{}  value:{} error",key,value,e);//不用getMessage是因为信息太少了
            RedisShardedJedisPool.returnBrokenResource(shardedJedis);
            return null;
        }finally{
            RedisShardedJedisPool.returnShardedJedis(shardedJedis);
        }
        return shardedJedisResult;
    }






    /*调试测试方法*/
    public static void main(String[] args){
        ShardedJedis shardedJedis=RedisShardedJedisPool.getShardedJedis();
        for (int i=0;i<=10;i++){
            RedisShardedJedisPoolUtil.setJedis("Q_Q-"+i,"YYY");
            String value= RedisShardedJedisPoolUtil.getJedis("Q_Q-"+i);
            System.out.println("打印:"+value);
        }

        //RedisShardedJedisPoolUtiltmp.setExJedis("Q_Q",60*1,value+"111");
        //RedisShardedJedisPoolUtiltmp.setExpireJedis("Q_Q",60*2);
        System.out.println("结束了");



        int j=0;
        for(int i=0;i<1000;i++) {
            j=j++;
        }
        System.out.println(j);

        log.info("测试是否空指针");
        Integer i = null;
        int k = i.intValue();

    }


}
