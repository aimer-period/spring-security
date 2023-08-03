package com.demo.springsecurity.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

@Slf4j
public class RedisUtil {

    @SuppressWarnings("unchecked")
    private static RedisTemplate<String, Object > redisTemplate = SpringContextUtil.getBean("redisTemplate");


    public static Boolean checkCache(String userId, String KEY) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        removeExpireToken(KEY);
        Double score = zSetOperations.score(KEY, userId);
        if (score != null) {
            return true;
        }
        return false;
    }

    public static void removeExpireToken(String KEY) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        // 移除超过9个登录信息的最旧记录,只保留九个数据
        if (zSetOperations.size(KEY) >= 10) {
            // 移除倒数第一到倒数第10的数据
            zSetOperations.removeRange(KEY, 0, -10);
        }
        // 移除过期的信息
        // 获取当前时间戳
        // 计算五分钟之前的时间戳
        long fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000);
        // 获取分数低于指定时间戳的元素集合
        // Double.NEGATIVE_INFINITY会拿集合中最低的分数，所以要保持一个，否则不管怎么样都会删除
        Set<Object> elementsToRemove = zSetOperations.rangeByScore(KEY, 1, fiveMinutesAgo);
        if (elementsToRemove.size() >= 1) {
            // 移除分数低于指定时间戳的元素
            long removedCount = zSetOperations.remove(KEY, elementsToRemove.toArray());
            System.out.println("移除的数据数量: " + removedCount);
        }
    }
}
