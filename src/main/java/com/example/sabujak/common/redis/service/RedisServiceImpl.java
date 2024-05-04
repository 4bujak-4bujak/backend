package com.example.sabujak.common.redis.service;

import com.example.sabujak.common.exception.CommonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.sabujak.common.exception.CommonErrorCode.COMMON_JSON_PROCESSING_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        log.info("get data from redis with key: {}, type: {}", key, type.getName());
        String value = (String) redisTemplate.opsForValue().get(key);
        try {
            return Optional.ofNullable(objectMapper.readValue(value, type));
        } catch (IllegalArgumentException e) {
            log.warn("value for key does not exist in redis", e);
            return Optional.empty();
        } catch (JsonProcessingException e) {
            log.error("error occurred while processing JSON", e);
            throw new CommonException(COMMON_JSON_PROCESSING_ERROR);
        }
    }

    @Override
    public void set(String key, Object data, Long expiration) {
        log.info("set data in redis with key: {}, data: {}, expiration: {} milliseconds", key, data, expiration);
        try {
            String value = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, value, expiration, TimeUnit.MILLISECONDS);
        } catch (JsonProcessingException e) {
            log.error("error occurred while processing JSON", e);
        }
    }

    @Override
    public void delete(String key) {
        log.info("delete data from redis with key: {}", key);
        redisTemplate.delete(key);
    }
}
