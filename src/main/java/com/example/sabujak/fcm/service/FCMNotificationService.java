package com.example.sabujak.fcm.service;

import com.example.sabujak.fcm.exception.FCMException;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.example.sabujak.fcm.constants.FCMConstants.*;
import static com.example.sabujak.fcm.exception.FCMErrorCode.*;
import static com.google.firebase.messaging.MessagingErrorCode.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMNotificationService {

    private final FCMTokenService fcmTokenService;
    private final FirebaseMessaging firebaseMessaging;

    public Message createFCMMessage(String email, String content, Long notificationId, String targetUrl) {
        return Message.builder()
                .setToken(fcmTokenService.getFCMToken(email))
                .setNotification(createNotification(content))
                .putAllData(createData(notificationId, targetUrl))
                .build();
    }

    private Notification createNotification(String body) {
        return Notification.builder()
                .setTitle(DEFAULT_TITLE)
                .setBody(body)
                .build();
    }

    private Map<String, String> createData(Long notificationId, String targetUrl) {
        return Map.of(
                NOTIFICATION_ID_KEY, notificationId.toString(),
                TARGET_URL_KEY, targetUrl
        );
    }

    @Retryable(
            retryFor = FirebaseMessagingException.class,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void sendFCMNotification(String email, Message message) throws FirebaseMessagingException {
        try {
            String response = firebaseMessaging.send(message);
            log.info("FCM Notification Sent Successfully. Message ID: [{}]", response);
        } catch (FirebaseMessagingException e) {
            MessagingErrorCode errorCode = e.getMessagingErrorCode();
            if (errorCode.equals(INTERNAL) || errorCode.equals(UNAVAILABLE)) {
                log.info("Failed To Send FCM Notification. FCM Server Error. Retrying...");
                throw e;
            } else if (errorCode.equals(UNREGISTERED) || errorCode.equals(INVALID_ARGUMENT)) {
                log.info("Failed To Send FCM Notification. Invalid FCM Token. Deleting...");
                fcmTokenService.removeFCMToken(email);
                throw new FCMException(INVALID_FCM_TOKEN);
            } else if (errorCode.equals(THIRD_PARTY_AUTH_ERROR) || errorCode.equals(SENDER_ID_MISMATCH)) {
                throw new FCMException(FCM_UNAUTHORIZED);
            }
        }
    }

    @Recover
    private void recover(FirebaseMessagingException e, String email, Message message) {
        throw new FCMException(FCM_SERVER_ERROR);
    }
}
