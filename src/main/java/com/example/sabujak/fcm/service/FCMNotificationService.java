package com.example.sabujak.fcm.service;

import com.example.sabujak.fcm.exception.FCMException;
import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static com.example.sabujak.fcm.constants.FCMConstants.*;
import static com.example.sabujak.fcm.exception.FCMErrorCode.*;
import static com.google.firebase.messaging.MessagingErrorCode.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMNotificationService {

    private final FCMTokenService fcmTokenService;
    private final FirebaseMessaging firebaseMessaging;

    private final Executor callBackTaskExecutor;
    private final ObjectProvider<FCMNotificationService> provider;

    public Message createFCMMessage(String email, String title, String content, Long targetId) {
        return Message.builder()
                .setToken(fcmTokenService.getFCMToken(email))
                .setNotification(createNotification(title, content))
                .putAllData(createData(targetId))
                .build();
    }

    private Notification createNotification(String title, String body) {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .setImage(DEFAULT_IMAGE)
                .build();
    }

    private Map<String, String> createData(Long targetId) {
        return Map.of(TARGET_ID_KEY, targetId.toString());
    }

    public void sendFCMNotificationAsync(String email, Message message) {
        ApiFuture<String> apiFuture = firebaseMessaging.sendAsync(message);
        apiFuture.addListener(() -> {
            try {
                String response = apiFuture.get();
                log.info("FCM Notification Sent Successfully. Message ID: [{}]", response);
                log.info("Current Call Back Thread Name: [{}]", Thread.currentThread().getName());
            } catch (InterruptedException | ExecutionException executionException) {
                if (executionException.getCause() instanceof FirebaseMessagingException firebaseMessagingException) {
                    MessagingErrorCode errorCode = firebaseMessagingException.getMessagingErrorCode();
                    if (isRetryMessagingErrorCode(errorCode)) {
                        provider.getObject().sendFCMNotification(email, message);
                        return;
                    }
                    handleFCMMessagingException(errorCode, email);
                }
            }
        }, callBackTaskExecutor);
    }

    @Retryable(
            retryFor = RuntimeException.class,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void sendFCMNotification(String email, Message message) {
        try {
            String response = firebaseMessaging.send(message);
            log.info("FCM Notification Sent Successfully. Message ID: [{}]", response);
        } catch (FirebaseMessagingException firebaseMessagingException) {
            MessagingErrorCode errorCode = firebaseMessagingException.getMessagingErrorCode();
            if (isRetryMessagingErrorCode(errorCode)) {
                throw new RuntimeException();
            }
            handleFCMMessagingException(errorCode, email);
        }
    }

    private boolean isRetryMessagingErrorCode(MessagingErrorCode errorCode) {
        if (errorCode.equals(INTERNAL) || errorCode.equals(UNAVAILABLE)) {
            log.info("Failed To Send FCM Notification. FCM Server Error. Retrying...");
            return true;
        }
        return false;
    }

    private void handleFCMMessagingException(MessagingErrorCode errorCode, String email) {
        if (errorCode.equals(UNREGISTERED) || errorCode.equals(INVALID_ARGUMENT)) {
            log.info("Failed To Send FCM Notification. Invalid FCM Token. Deleting...");
            fcmTokenService.removeFCMToken(email);
            throw new FCMException(INVALID_FCM_TOKEN);
        } else if (errorCode.equals(THIRD_PARTY_AUTH_ERROR) || errorCode.equals(SENDER_ID_MISMATCH)) {
            log.info("Failed To Send FCM Notification. FCM Service Configuration or Permission Error");
            throw new FCMException(FCM_UNAUTHORIZED);
        }
    }

    @Recover
    private void recover(RuntimeException exception, String email, Message message) {
        throw new FCMException(FCM_SERVER_ERROR);
    }
}
