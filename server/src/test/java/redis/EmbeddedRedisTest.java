import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import redis.embedded.RedisServer;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = EmbeddedRedisTest.TestConfig.class)
public class EmbeddedRedisTest {

    private static RedisServer redisServer;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeAll
    public static void setUp() throws IOException {
        redisServer = new RedisServer(6379); // Uruchamiamy Redis na porcie 6379
        redisServer.start();
    }

    @AfterAll
    public static void tearDown() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    @Test
    public void testRedisSetAndGet() {
        redisTemplate.opsForValue().set("key", "value");
        Object v = redisTemplate.opsForValue().get("key");
        assertThat(v).isEqualTo("value");
    }

    public static class TestConfig {
        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            return new LettuceConnectionFactory("localhost", 6379);
        }

        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
            RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(factory);

            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new StringRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setHashValueSerializer(new StringRedisSerializer());

            template.afterPropertiesSet();
            return template;
        }
    }

}
