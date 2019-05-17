package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: hexiaoyan
 * @Date: 2019/5/13 21:56
 * @Description:
 */
public class TokenCache {
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    public static final String TOKEN_PREFIX = "token_";
    private static LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
        @Override
        public String load(String s) throws Exception {
            return "null";
        }
    });

    public static void setKey(String key, String value) {
        loadingCache.put(key, value);

    }

    public static String getKey(String key) {
        String value = null;
        try {
            value = loadingCache.get(key);
        } catch (ExecutionException e) {
            logger.error("TokenCache get key occur error!");
        }
        if ("null".equals(value)) {
            return null;
        }
        return value;
    }
}
