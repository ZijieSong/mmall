package com.mmall.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CacheUtil {
    private static Logger logger = LoggerFactory.getLogger(CacheUtil.class);

    public static final String TOKEN_PREFIX = "token_";

    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder()
            .initialCapacity(1000).maximumSize(10000).expireAfterAccess(12,TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                @Override
                //该方法的作用是，当loadingCache的get方法传入的key找不到对应的value，就执行该方法
                //默认是返回null，但是对于方法“V get(key, loader) throws ExecutionException”loader的返回值不能为null，否则会抛出异常
                //为了避免抛出异常，也就不能在不存在key对应的value时返回null，因此返回一个字符串"null"
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void put(String key, String value){
        localCache.put(key,value);
    }

    public static String get(String key){
        try {
            //这样如果能取到值，就直接返回，如果取不到，走上面的load方法，返回一个"null"，不会报异常，并传出一个null
            String value = localCache.get(key);
            if(StringUtils.equals(value,"null"))
                return null;
            return value;
        } catch (ExecutionException e) {
            logger.error("loadingCache get error",e);
        }
        return null;
    }
}
