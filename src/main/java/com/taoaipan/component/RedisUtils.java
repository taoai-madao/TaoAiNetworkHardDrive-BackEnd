package com.taoaipan.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author TaoAi
 * @Date 2023/12/7 11:22
 * @description Redis 工具类
 */
@Component("redisUtils")
public class RedisUtils<V> {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    @Resource
    private RedisTemplate<String, V> redisTemplate;

    /**
     * 普通缓存写入redis
     * @param key 键
     * @param value 值
     * @return true / false
     */
    public boolean set(String key, V value){
        try{
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("设置redisKey:{},value:{}失败", key, value);
            return false;
        }
    }

    /**
     * 写入缓存与失效时间
     * @param key 健
     * @param value 值
     * @param time 有效时间
     * @return true / false
     */
    public boolean setex(String key, V value, long time){
        try{
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置redisKey:{},value:{}失败", key, value);
            return false;
        }
    }

    /**
     * 根据健获取值
     * @param key 健
     * @return 返回健在redis缓存中的值
     */
    public V get(String key){
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    public void delete(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }
}
