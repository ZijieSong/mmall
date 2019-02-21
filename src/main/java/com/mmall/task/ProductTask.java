package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.service.OrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.ShardedRedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class ProductTask {

    @Resource(name = "orderService")
    private OrderService orderService;

    //    @Scheduled(cron = "0 0/1 * * * ? ")
    public void closeOrderV1() {
        log.info("开始定时关单");
        orderService.closeOrder(Integer.valueOf(PropertiesUtil.get("order.expireTime", "2")));
        log.info("结束定时关单");
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void closeOrderV2() {
        //开始分布式锁的获取
        Long lockExpireTime = Long.valueOf(PropertiesUtil.get("order.close.redisLock.expireTime", "5000"));
        Long setnxResult = ShardedRedisUtil.setnx(Const.RedisLockKey.CLOSE_ORDER_LOCK, String.valueOf(System.currentTimeMillis() + lockExpireTime));
        if (setnxResult != null && (setnxResult.intValue() == 1)) {
            //成功获取锁
            log.info("{},获取锁成功",System.currentTimeMillis());
            closeOrder(lockExpireTime);
        } else {
            //查看是否有获取锁的权利,即判断当前锁是否由于特殊原因已经达到了超时时间但是未释放锁
            String expireTime = ShardedRedisUtil.get(Const.RedisLockKey.CLOSE_ORDER_LOCK);
            if (expireTime == null || (System.currentTimeMillis() > Long.valueOf(expireTime))) {
                //已经可以释放锁了，尝试获取锁
                if (tryLock(expireTime, lockExpireTime))
                    closeOrder(lockExpireTime);
                else
                    log.info("{},尝试获取过期锁失败",System.currentTimeMillis());
            }else
                log.info("{},锁还未过期，无法获取",System.currentTimeMillis());
        }
    }

    private Integer millisToSeconds(Long time) {
        return BigDecimalUtil.divide(Double.valueOf(time), (double) 1000).intValue();
    }

    private void closeOrder(Long lockExpireTime) {
        //首先设置锁的超时时间，防止锁由于操作抛异常而未被删除导致的死锁
        ShardedRedisUtil.expire(Const.RedisLockKey.CLOSE_ORDER_LOCK, millisToSeconds(lockExpireTime));
        log.info("{},开始定时关单",System.currentTimeMillis());
//        orderService.closeOrder(Integer.valueOf(PropertiesUtil.get("order.expireTime", "2")));
        //模拟关单操作, 睡1s
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("sleep exception",e);
        }
        log.info("{},结束定时关单",System.currentTimeMillis());
        //释放锁
        ShardedRedisUtil.del(Const.RedisLockKey.CLOSE_ORDER_LOCK);
        log.info("{},释放锁成功",System.currentTimeMillis());
    }

    private boolean tryLock(String oldValue, Long lockExpireTime) {
        //如果传入的oldValue为null，代表锁已被删除，则getset若返回null，获取锁成功
        //如果不为null，则尝试获取，如果新旧值相等，则获取成功
        //如果传入的oldValue不为null，但是getSet获取的旧值为空，还是锁已经被删除了，则获取锁成功
        String newValue = ShardedRedisUtil.getSet(Const.RedisLockKey.CLOSE_ORDER_LOCK, String.valueOf(System.currentTimeMillis() + lockExpireTime));
        return newValue == null || StringUtils.equals(oldValue, newValue);
    }
}
