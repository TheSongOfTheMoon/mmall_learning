package com.mmall.task;

import com.mmall.common.Conts;
import com.mmall.common.RedissonManager;
import com.mmall.service.impl.IOrderServiceImpl;
import com.mmall.utils.PropertiesUtil;
import com.mmall.utils.RedisShardedJedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderServiceImpl iOrderService;

    @Autowired
    private RedissonManager redissonManager;

    //@Scheduled(cron="0/59 * * * * ? ")//每分钟启动一次(每个一分钟的整数倍启动一次)
    public void closeOrderTaskV1(){
        log.info("定时调度--->关闭超时订单");
        Integer hour=Integer.parseInt(PropertiesUtil.getProperties("task.colse.order.time.hour","2"));
        //iOrderService.CloseOrderTask(hour);
        //此处也可以维护一张关闭订单的操作表
        log.info("定时调度--->关闭订单结束");
    }


    //分布式锁
    //@Scheduled(cron="0/59 * * * * ? ")//每分钟启动一次(每个一分钟的整数倍启动一次)
    public void closeOrderTaskV2(){
        log.info("定时调度--->分布式锁");
        Long timeout=Long.parseLong(PropertiesUtil.getProperties("task.close.lock.timeout","5000"));

        Long setNxResult= RedisShardedJedisPoolUtil.setNxJedis(Conts.REDIS_LOCK.TASK_CLOSE_ORDER_LOCK,String.valueOf(System.currentTimeMillis()+timeout));
        if (setNxResult!=null&&setNxResult.intValue()==1){
            //返回值1表示锁标志设置成功,可以获取锁
            this.CloseOrder(Conts.REDIS_LOCK.TASK_CLOSE_ORDER_LOCK);
        }else{
            log.info("没有获取到分布式锁：{}",Conts.REDIS_LOCK.TASK_CLOSE_ORDER_LOCK);
        }
        //此处也可以维护一张关闭订单的操作表
        log.info("定时调度--->关闭订单结束");
    }



    //分布式锁
    //@Scheduled(cron="0/59 * * * * ? ")//每分钟启动一次(每个一分钟的整数倍启动一次)
    public void closeOrderTaskV3(){
        log.info("定时调度--->分布式锁");
        Long timeout=Long.parseLong(PropertiesUtil.getProperties("task.close.lock.timeout","5000"));

        Long setNxResult= RedisShardedJedisPoolUtil.setNxJedis(Conts.REDIS_LOCK.TASK_CLOSE_ORDER_LOCK,String.valueOf(System.currentTimeMillis()+timeout));
        if (setNxResult!=null&&setNxResult.intValue()==1){
            //返回值1表示锁标志设置成功,可以获取锁
            this.CloseOrder(Conts.REDIS_LOCK.TASK_CLOSE_ORDER_LOCK);
        }else{
            log.info("锁冲突，开始判断");
            //尝试原有key值
            String lockValueStr=RedisShardedJedisPoolUtil.getJedis(Conts.REDIS_LOCK.TASK_CLOSE_ORDER_LOCK);

            //如果查询到数据并且锁已经失效了
            if (lockValueStr!=null&&System.currentTimeMillis()>Long.parseLong(lockValueStr)){
                String getSetResult=RedisShardedJedisPoolUtil.GetOldValNorSetValJedis(Conts.REDIS_LOCK.TASK_CLOSE_ORDER_LOCK,String.valueOf(System.currentTimeMillis()+timeout));
                if (getSetResult==null||(getSetResult!=null&& StringUtils.equals(lockValueStr,getSetResult))){
                    this.CloseOrder(Conts.REDIS_LOCK.TASK_CLOSE_ORDER_LOCK);
                    log.info("获取到锁，开始处理");
                }else{
                    log.info("没有获取到分布式锁：{}",Conts.REDIS_LOCK.TASK_CLOSE_ORDER_LOCK);
                }
            }else{
                log.info("没有获取到分布式锁：{}",Conts.REDIS_LOCK.TASK_CLOSE_ORDER_LOCK);
            }

        }
        //此处也可以维护一张关闭订单的操作表
        log.info("定时调度--->关闭订单结束");
    }



    //分布式锁
    @Scheduled(cron="0/59 * * * * ? ")//每分钟启动一次(每个一分钟的整数倍启动一次)
    public void closeOrderTaskV4() {
        log.info("定时调度--->分布式锁");
        RLock rLock=redissonManager.getRedisson().getLock(Conts.REDIS_LOCK.TASK_CLOSE_ORDER_LOCK);
        boolean getLock=false;
        try {
            if (rLock.tryLock(0,5, TimeUnit.SECONDS)){
                log.info("Redisson获取到分布式锁 {} ThreadName ：{}",Conts.REDIS_LOCK.TASK_CLOSE_ORDER_LOCK,Thread.currentThread().getName());
                Integer hour=Integer.parseInt(PropertiesUtil.getProperties("task.colse.order.time.hour","2"));
                iOrderService.CloseOrderTask(hour);
            }else{
                log.info("Redisson获取不到分布式锁 {} ThreadName ：{}",Conts.REDIS_LOCK.TASK_CLOSE_ORDER_LOCK,Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            log.info("Redisson获取不到分布式锁异常",e);
        }finally{/*伪代码的健壮性*/
            if (!getLock){
                return;
            }
            rLock.unlock();
            log.info("Redisson分布式锁释放");

        }
    }


    //关闭订单
    private void CloseOrder(String lockName){
        RedisShardedJedisPoolUtil.setExpireJedis(lockName,5);
        log.info("获取 {} ThreadName ：{}",lockName,Thread.currentThread().getName());
        Integer hour=Integer.parseInt(PropertiesUtil.getProperties("task.colse.order.time.hour","2"));
        iOrderService.CloseOrderTask(hour);
        RedisShardedJedisPoolUtil.delJedis(lockName);
        log.info("释放 {} ThreadName ：{}",lockName,Thread.currentThread().getName());
        log.info("定时调度------分布式锁----定时关单结束");

    }

    //非正常流程关闭项目,但tomcat进程仍然存货,可触发阻止
    @PreDestroy
    private void delLock(){
        RedisShardedJedisPoolUtil.delJedis(Conts.REDIS_LOCK.TASK_CLOSE_ORDER_LOCK);
    }

}
