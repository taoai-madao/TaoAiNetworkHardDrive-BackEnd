package com.taoaipan.entity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @version 1.0
 * @Author TaoAi
 * @Date 2024/1/1 15:24
 * @description Redis 工具类序列化
 */
@Configuration("redisConfig")
public class RedisConfig<V> {
    @Bean
    public RedisTemplate<String, V> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, V> template = new RedisTemplate<>();
        // 设置RedisTemplate的连接工厂
        template.setConnectionFactory(factory);
        // 设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        // 设置Value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        // 设置hash的Key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());
        template.afterPropertiesSet();
        return template;
    }
}
