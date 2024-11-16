package org.crochet.service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration DEFAULT_TTL = Duration.ofMinutes(30);

    /**
     * Constructor for CacheService
     * 
     * @param redisTemplate Redis template
     * @param objectMapper Object mapper
     */
    public CacheService(RedisTemplate<String, Object> redisTemplate,
                       ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Set a value in the cache with a default TTL
     * 
     * @param key cache key
     * @param value value to store
     */
    public void set(String key, Object value) {
        set(key, value, DEFAULT_TTL);
    }
    
    /**
     * Set a value in the cache with a custom TTL
     * 
     * @param key cache key
     * @param value value to store
     * @param ttl expiration time
     */
    public void set(String key, Object value, Duration ttl) {
        try {
            if (value != null) {
                String jsonValue = objectMapper.writeValueAsString(value);
                redisTemplate.opsForValue().set(key, jsonValue, ttl);
                log.debug("Stored in cache: {}", key);
            }
        } catch (Exception e) {
            log.error("Error storing in cache: {}", key, e);
        }
    }
    
    /**
     * Get a value from the cache
     * 
     * @param key cache key
     * @param type class type
     * @return Optional<T>
     */
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                if (value instanceof String) {
                    return Optional.of(objectMapper.readValue((String) value, type));
                }
                return Optional.of(objectMapper.convertValue(value, type));
            }
        } catch (Exception e) {
            log.error("Error retrieving from cache: {}", key, e);
        }
        return Optional.empty();
    }

    /**
     * Delete a key from the cache
     *
     * @param key cache key
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Deleted from cache: {}", key);
        } catch (Exception e) {
            log.error("Error deleting from cache: {}", key, e);
        }
    }
    
    /**
     * Invalidate a cache by pattern
     * 
     * @param pattern pattern cache key
     */
    public void invalidateCache(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Deleted keys matching pattern: {}", pattern);
            }
        } catch (Exception e) {
            log.error("Error deleting by pattern: {}", pattern, e);
        }
    }

    /**
     * Check if a key exists in the cache
     * 
     * @param key cache key
     * @return boolean
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Error checking key existence: {}", key, e);
            return false;
        }
    }

    /**
     * Set expiration time for a key
     * 
     * @param key cache key
     * @param timeout expiration time
     * @return boolean
     */
    public boolean expire(String key, Duration timeout) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout));
        } catch (Exception e) {
            log.error("Error setting expiration for key: {}", key, e);
            return false;
        }
    }
}
