package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
public class RedisUtil {

    public static String set(String key, String value) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getResource();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("redis error when set:{} value:{}", key, value, e);
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String get(String key) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getResource();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("redis error when get:{}", key, e);
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String setex(String key, String value, Integer seconds) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getResource();
            result = jedis.setex(key, seconds, value);
        } catch (Exception e) {
            log.error("redis error when setex:{} value:{}", key, value, e);
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static Long expire(String key, Integer seconds) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = RedisPool.getResource();
            result = jedis.expire(key, seconds);
        } catch (Exception e) {
            log.error("redis error when expire:{}", key, e);
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = RedisPool.getResource();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("redis error when delete:{}", key, e);
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }


    public static void main(String[] args){
        RedisUtil.set("abc","abc");
        RedisUtil.get("abc");
        RedisUtil.setex("expire","expire",60);
        RedisUtil.expire("abc",120);
        RedisUtil.del("abc");

    }
}
