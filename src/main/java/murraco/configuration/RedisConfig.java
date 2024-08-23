package murraco.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public <K, V> RedisTemplate<K, V> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<K, V> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 设置键的序列化器为字符串序列化器
        template.setKeySerializer(new StringRedisSerializer());

        // 设置值的序列化器为通用的 JSON 序列化器
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 如果需要处理哈希类型的数据，也可以设置哈希键值的序列化器
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}
