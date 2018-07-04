package com.mmall.task;

import com.mmall.service.impl.IOrderServiceImpl;
import com.mmall.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderServiceImpl iOrderService;

    @Scheduled(cron="0/59 * * * * ? ")//每分钟启动一次(每个一分钟的整数倍启动一次)
    public void closeOrderTaskV1(){
        log.info("定时调度--->关闭超时订单");
        Integer hour=Integer.parseInt(PropertiesUtil.getProperties("task.colse.order.time.hour","2"));
        //iOrderService.CloseOrderTask(hour);
        //此处也可以维护一张关闭订单的操作表
        log.info("定时调度--->关闭订单结束");
    }
}
