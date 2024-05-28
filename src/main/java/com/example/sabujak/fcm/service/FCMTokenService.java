package com.example.sabujak.fcm.service;

import com.example.sabujak.common.redis.service.RedisService;
import com.example.sabujak.fcm.dto.SaveFCMTokenRequest;
import com.example.sabujak.fcm.exception.FCMException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.example.sabujak.fcm.constants.FCMConstants.FCM_TOKEN_EXPIRATION;
import static com.example.sabujak.fcm.constants.FCMConstants.FCM_TOKEN_PREFIX;
import static com.example.sabujak.fcm.exception.FCMErrorCode.FCM_TOKEN_NOT_FOUND;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMTokenService {

    private final RedisService redisService;

    public String getFCMToken(String email) {
        log.info("Getting FCM Token. Member Email: [{}]", email);
        String key = FCM_TOKEN_PREFIX + email;
        return redisService.get(key, String.class)
                .orElseThrow(() -> new FCMException(FCM_TOKEN_NOT_FOUND));
    }

    public void saveFCMToken(SaveFCMTokenRequest saveFCMTokenRequest, String email) {
        String key = FCM_TOKEN_PREFIX + email;
        String value = saveFCMTokenRequest.fcmToken();
        redisService.set(key, value, FCM_TOKEN_EXPIRATION);
        log.info("Saved FCM Token. Member Email: [{}]", email);
    }

    public void removeFCMToken(String email) {
        String key = FCM_TOKEN_PREFIX + email;
        redisService.delete(key);
        log.info("Removed FCM Token. Member Email: [{}]", email);
    }
}
