package com.microservice.oauth2.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {
    private String host;
    private Integer port;
    private Long expiredTime;
}
