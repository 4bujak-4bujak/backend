package com.example.sabujak.common.redis.service;

import java.util.Optional;

public interface RedisService {

    <T> Optional<T> get(String key, Class<T> type);

    void set(String key, Object data, Long expiration);

    void delete(String key);
}
