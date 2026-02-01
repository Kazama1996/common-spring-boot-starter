package com.kazama.common.snowflake;

import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(EntityManager.class)
@EnableConfigurationProperties(SnowflakeProperties.class)
public class SnowflakeAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public SnowflakeGenerator snowflakeGenerator(SnowflakeProperties properties){
        return new SnowflakeGenerator(properties.getWorkerId(), properties.getDatacenterId());
    }

    @Bean
    @ConditionalOnMissingBean
    public SnowflakeEntityListener snowflakeEntityListener(){
        return  new SnowflakeEntityListener();
    }
}
