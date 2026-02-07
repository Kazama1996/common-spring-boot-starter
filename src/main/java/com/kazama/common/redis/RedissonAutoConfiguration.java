package com.kazama.common.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(RedissonClient.class)
@EnableConfigurationProperties(RedisProperties.class)
public class RedissonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RedissonClient redissonClient(RedisProperties properties) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(properties.getAddress())
                .setPassword(properties.getPassword())
                .setDatabase(properties.getDatabase())
                .setConnectionPoolSize(properties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(properties.getConnectionMinimumIdleSize());
        return Redisson.create(config);
    }
}
