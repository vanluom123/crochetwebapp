package org.crochet.config;

import org.crochet.properties.RedisCacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisCacheConfig {

    @Bean
    @ConfigurationProperties(prefix = "redis")
    public RedisCacheProperties redisCacheProperties() {
        return new RedisCacheProperties();
    }

    @Bean
    public RedisTemplate<String, Object> redisCacheTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        RedisSerializer<Object> valueSerializer = new GenericJackson2JsonRedisSerializer();

        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);

        template.setHashKeySerializer(keySerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
        conf.setHostName(redisCacheProperties().getHost());
        conf.setPort(redisCacheProperties().getPort());
        conf.setUsername(redisCacheProperties().getUsername());
        conf.setPassword(redisCacheProperties().getPassword());
        var jedisClientConfBuilder = JedisClientConfiguration.builder();
        if (redisCacheProperties().isSsl()) {
            jedisClientConfBuilder.useSsl();
        }
        jedisClientConfBuilder.usePooling();
        JedisClientConfiguration jedisClientConf = jedisClientConfBuilder.build();
        return new JedisConnectionFactory(conf, jedisClientConf);
    }
}
