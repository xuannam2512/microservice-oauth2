package com.microservice.oauth2.redis.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisProperties redisProperties;

    /**
     * Connection factory.
     *
     * @return Redis Standard configuration
     */
    @Bean
    @Primary
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(
                new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort()));
    }

    /**
     * Create Redis template.
     *
     * @return Redis template
     */
    @Bean
    public <T> RedisTemplate<String, T> redisTemplate() {
        final RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
