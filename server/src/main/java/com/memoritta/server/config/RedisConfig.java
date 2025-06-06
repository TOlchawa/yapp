package com.memoritta.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, byte[]> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());

        // Use raw binary serializer for values (byte[])
        template.setValueSerializer(new org.springframework.data.redis.serializer.RedisSerializer<byte[]>() {
            @Override
            public byte[] serialize(byte[] bytes) {
                return bytes; // No conversion needed
            }

            @Override
            public byte[] deserialize(byte[] bytes) {
                return bytes; // No conversion needed
            }
        });

        template.afterPropertiesSet();
        return template;
    }
}
