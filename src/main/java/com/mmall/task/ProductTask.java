package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.service.OrderService;
import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class ProductTask {

    @Resource(name = "orderService")
    private OrderService orderService;

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void closeOrderV1(){
        log.info("开始定时关单");
        orderService.closeOrder(Integer.valueOf(PropertiesUtil.get("order.expireTime","2")));
        log.info("结束定时关单");
    }
}
