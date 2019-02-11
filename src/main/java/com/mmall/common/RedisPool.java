package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    private static JedisPool pool;
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

    private static String ip = PropertiesUtil.get("redis.ip");
    private static Integer port = Integer.valueOf(PropertiesUtil.get("redis.port", "6379"));

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

        pool = new JedisPool(jedisPoolConfig, ip, port, 2 * 1000);
    }


    public static Jedis getResource(){
        return pool.getResource();
    }

    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }


}
