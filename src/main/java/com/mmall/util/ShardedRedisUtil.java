package com.mmall.util;

import com.mmall.common.RedisPool;
import com.mmall.common.ShardedRedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Sharded;

@Slf4j
public class ShardedRedisUtil {

    public static String set(String key, String value) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = ShardedRedisPool.getResource();
            result = jedis.set(key, value);
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            log.error("redis error when set:{} value:{}", key, value, e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
        return result;
    }

    public static String get(String key) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = ShardedRedisPool.getResource();
            result = jedis.get(key);
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            log.error("redis error when get:{}", key, e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
        return result;
    }

    public static String setex(String key, String value, Integer seconds) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = ShardedRedisPool.getResource();
            result = jedis.setex(key, seconds, value);
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            log.error("redis error when setex:{} value:{}", key, value, e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
        return result;
    }

    public static Long expire(String key, Integer seconds) {
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = ShardedRedisPool.getResource();
            result = jedis.expire(key, seconds);
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            log.error("redis error when expire:{}", key, e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
        return result;
    }

    public static Long del(String key) {
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = ShardedRedisPool.getResource();
            result = jedis.del(key);
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            log.error("redis error when delete:{}", key, e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
        return result;
    }

    public static Long setnx(String key, String value) {
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = ShardedRedisPool.getResource();
            result = jedis.setnx(key,value);
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            log.error("redis error when setnx:{}", key, e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
        return result;
    }

    public static String getSet(String key, String value) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = ShardedRedisPool.getResource();
            result = jedis.getSet(key,value);
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            log.error("redis error when getset:{}", key, e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
        return result;
    }


    public static void main(String[] args){
        for(int i =0 ; i<10 ; i++){
            ShardedRedisUtil.set("key"+i,"value"+i);
        }
        for(int i =0 ; i<10; i++){
            log.info("key:{},value:{}","key"+i,ShardedRedisUtil.get("key"+i));
        }
    }
}
