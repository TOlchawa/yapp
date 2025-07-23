package com.memoritta.server.manager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.test.context.ContextConfiguration;
import redis.embedded.RedisServer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = BinaryDataManagerTest.Config.class)
class BinaryDataManagerTest {
    private static RedisServer redisServer;

    @BeforeAll
    static void start() throws Exception {
        redisServer = new RedisServer(7778);
        redisServer.start();
    }

    @AfterAll
    static void stop() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    @Configuration
    static class Config {
        @Bean
        RedisConnectionFactory connectionFactory() {
            return new LettuceConnectionFactory("localhost", 7778);
        }

        @Bean
        BinaryDataManager binaryDataManager(RedisConnectionFactory factory) {
            return new BinaryDataManager(new org.springframework.data.redis.core.RedisTemplate<>() {{
                setConnectionFactory(factory);
                afterPropertiesSet();
            }});
        }
    }

    @Autowired
    private BinaryDataManager binaryDataManager;

    @Test
    void saveAndLoad_returnsSameBytes() {
        byte[] data = new byte[] {1, 2, 3};
        java.util.UUID id = binaryDataManager.save(data);
        byte[] loaded = binaryDataManager.load(id);
        assertThat(loaded).containsExactly(data);
    }
}
