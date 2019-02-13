package com.mmall.common;

import com.google.common.collect.Lists;
import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.ShardInfo;
import redis.clients.util.Sharded;

import java.util.List;

public class ShardedRedisPool {
    private static ShardedJedisPool pool;
    //最大连接数
    private static Integer maxTotal = Integer.valueOf(PropertiesUtil.get("redis.maxTotal", "20"));
    //在jedispool中最大的idle状态(空闲的)的jedis实例的个数
    private static Integer maxIdle = Integer.valueOf(PropertiesUtil.get("redis.maxIdle", "10"));
    //在jedispool中最小的idle状态(空闲的)的jedis实例的个数
    private static Integer minIdle = Integer.valueOf(PropertiesUtil.get("redis.minIdle", "5"));
    //若为true，在从连接池拿jedis实例的时候进行test，检验该实例是否可用。这样拿到的jedis实例肯定可用，若为false不会检验
    private static Boolean testOnBorrow = Boolean.valueOf(PropertiesUtil.get("redis.testOnBorrow", "true"));
    //若为true，则在把jedis实例放回连接池的时候进行检验，看该实例是否可用，若可用则放回。
    private static Boolean testOnReturn = Boolean.valueOf(PropertiesUtil.get("redis.testOnReturn", "false"));

    private static String ip1 = PropertiesUtil.get("redis.ip1");
    private static Integer port1 = Integer.valueOf(PropertiesUtil.get("redis.port1", "6379"));
    private static String ip2 = PropertiesUtil.get("redis.ip2");
    private static Integer port2 = Integer.valueOf(PropertiesUtil.get("redis.port2", "6380"));

    static {
        generatePool();
    }

    private static void generatePool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);

        //连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。
        jedisPoolConfig.setBlockWhenExhausted(true);

        JedisShardInfo info1 = new JedisShardInfo(ip1, port1, 2 * 1000);
        JedisShardInfo info2 = new JedisShardInfo(ip2, port2, 2 * 1000);
        List<JedisShardInfo> jedisShardInfoList = Lists.newArrayList();
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        //murmur是一致性hash算法
        pool = new ShardedJedisPool(jedisPoolConfig,jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }


    public static ShardedJedis getResource() {
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis) {
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis) {
        pool.returnBrokenResource(jedis);
    }


}
