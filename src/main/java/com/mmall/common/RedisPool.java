package com.mmall.common;

import com.mmall.utils.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {

    private static JedisPool pool;//jedis连接池
    private static RedisPool redisPool;//静态代码以保证项目启动就会被加载
    private static Integer maxTotal= Integer.parseInt(PropertiesUtil.getProperties("redis.max.Total","20"));//允许的最大连接数
    private static Integer maxIdel=Integer.parseInt(PropertiesUtil.getProperties("redis.max.Idel","10"));//当前最大空闲的实例个数
    private static Integer minIdel=Integer.parseInt(PropertiesUtil.getProperties("redis.min.Idel","2"));//当前最小空闲的实例个数

    private static Boolean testOneBorrow=Boolean.parseBoolean(PropertiesUtil.getProperties("redis.test.borrow","true"));//在获取一个实例的时候，判断是否一定是可用实例(是否做验证)，如果为true肯定是可以用
    private static Boolean testOneReturn=Boolean.parseBoolean(PropertiesUtil.getProperties("redis.test.return","true"));//在返回一个实例的时候，判断是否一定是可用实例(是否做验证)，如果为true肯定是可以用

    private static String  redisIp=PropertiesUtil.getProperties("redis.pool.ip");
    private static Integer redisPort=Integer.parseInt(PropertiesUtil.getProperties("redis.pool.port","6379"));

    /*初始化为私有*/
    private static void initPool(){
        JedisPoolConfig config=new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdel);
        config.setMinIdle(minIdel);
        config.setTestOnBorrow(testOneBorrow);
        config.setTestOnReturn(testOneReturn);

        //设置参数当超过连接池数据量是否阻塞直到超时(true),false会超出异常
        config.setBlockWhenExhausted(true);

        //启动连接池
        pool=new JedisPool(config,redisIp,redisPort,1000*Integer.parseInt(PropertiesUtil.getProperties("redis.pool.timeout","2")));

    }

    /*初始化*/
    static {
        initPool();
    }


    /*从连接池获取一个jedis实例*/
    public static Jedis getJedis(){
        return pool.getResource();
    }

    /*将实例放置回连接池*/
    public static void returnJedis(Jedis jedis){
        if (jedis!=null){
            pool.returnResource(jedis);
        }
    }

    /*将坏的实例放置回连接池*/
    public static void returnBrokenResource(Jedis jedis){
        if (jedis!=null){
            pool.returnBrokenResource(jedis);
        }
    }


    public static void main(String[] args){
        Jedis jedis=pool.getResource();
        jedis.set("cyq","cyvalue1");
        returnJedis(jedis);

        pool.destroy();//临时调用，销毁连接池中所有连接
    }


}
