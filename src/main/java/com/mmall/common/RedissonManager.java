package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RedissonManager {
    private Redisson redisson = null;

    private Config config = new Config();

    private String host = PropertiesUtil.get("redis.ip");
    private String port = PropertiesUtil.get("redis.port");

    @PostConstruct
    public void init(){
        config.useSingleServer().setAddress(host+":"+port);
        redisson = (Redisson) Redisson.create(config);
    }

    public Redisson getRedisson(){
        return redisson;
    }

}
