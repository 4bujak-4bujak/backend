package com.example.sabujak;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test");
    }

    @GetMapping("/redis-test")
    public ResponseEntity<String> redis() {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set("test", "test");
        return ResponseEntity.ok((String)operations.get("test"));
    }
}
