//package com.example.sabujak.common.redis.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.testcontainers.containers.GenericContainer;
//
//@Configuration
//@Profile({"default", "local"})
//public class EmbeddedRedisConfig {
//
//    private static final String REDIS_IMAGE = "redis:7.0.8-alpine";
//    private static final int REDIS_PORT = 6379;
//    private static final GenericContainer REDIS_CONTAINER;
//
//    static {
//        REDIS_CONTAINER = new GenericContainer(REDIS_IMAGE)
//                .withExposedPorts(REDIS_PORT)
//                .withReuse(true);
//        REDIS_CONTAINER.start();
//
//        System.setProperty("spring.redis.host", REDIS_CONTAINER.getHost());
//        System.setProperty("spring.redis.port", REDIS_CONTAINER.getMappedPort(REDIS_PORT).toString());
//    }
//}
