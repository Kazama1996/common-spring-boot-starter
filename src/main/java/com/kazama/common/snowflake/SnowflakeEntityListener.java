package com.kazama.common.snowflake;

import jakarta.persistence.PrePersist;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class SnowflakeEntityListener {

    private static SnowflakeGenerator generator;

    @Autowired
    public void setGenerator(SnowflakeGenerator gen){
        generator = gen;
    }




    @PrePersist
    public void generateId(Object entity) {
        Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(SnowflakeId.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        if (field.get(entity) == null) {
                            field.set(entity, generator.nextId());
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
