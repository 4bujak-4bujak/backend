package com.example.sabujak.fcm.service;

import com.example.sabujak.fcm.exception.FCMException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.example.sabujak.fcm.constants.FCMConstants.*;
import static com.example.sabujak.fcm.exception.FCMErrorCode.FCM_SENDING_MESSAGE_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMNotificationService {

    private final FCMTokenService FCMTokenService;
    private final FirebaseMessaging firebaseMessaging;

    public void sendFCMNotification(String email, String content, String notificationId, String targetUrl) {
        log.info("Sending FCM Notification. Receiver Email: [{}]", email);
        String token = getFCMToken(email);
        Notification notification = createNotification(DEFAULT_TITLE, content);
        Map<String, String> data = createData(notificationId, targetUrl);
        Message message = createMessage(token, notification, data);
        try {
            String response = firebaseMessaging.send(message);
            log.info("FCM Notification Sent Successfully. Message ID: [{}]", response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed To Send FCM Notification. Exception Message: [{}]", e.getMessage());
            throw new FCMException(FCM_SENDING_MESSAGE_FAILED);
        }
    }

    private String getFCMToken(String email) {
        return FCMTokenService.getFCMToken(email);
    }

    private Notification createNotification(String title, String body) {
        log.info("Creating Notification. Notification Title: [{}}, Body: [{}]", title, body);
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }

    private Map<String, String> createData(String notificationId, String targetUrl) {
        log.info("Creating Data. Notification ID: [{}], Target URL: [{}]", notificationId, targetUrl);
        return Map.of(
                NOTIFICATION_ID_KEY, notificationId,
                TARGET_URL_KEY, targetUrl
        );
    }

    private Message createMessage(String token, Notification notification, Map<String, String> data) {
        return Message.builder()
                .setToken(token)
                .setNotification(notification)
                .putAllData(data)
                .build();
    }
}
