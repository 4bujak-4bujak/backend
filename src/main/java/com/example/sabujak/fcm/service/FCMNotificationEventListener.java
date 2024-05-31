package com.example.sabujak.fcm.service;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.notification.service.NotificationService;
import com.example.sabujak.post.dto.CommentCreatedEvent;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static java.lang.Thread.currentThread;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMNotificationEventListener {

    private final NotificationService notificationService;
    private final FCMNotificationService fcmNotificationService;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Async
    public void saveNotificationAndSendFCMNotificationForComment(CommentCreatedEvent event)
            throws FirebaseMessagingException {
        String email = event.receiverEmail();
        String content = event.notificationContent();
        String targetUrl = event.targetUrl();
        log.info(
                "Preparing FCM Notification For Comment. " +
                "Receiver Email: [{}], Notification Content: [{}], Target URL: [{}]", email, content, targetUrl
        );

        saveNotification(content, targetUrl, event.receiver());
        sendFCMNotification(email, createFCMMessage(email, content, targetUrl));
    }

    private void saveNotification(String content, String targetUrl, Member member) {
        log.info("Start Saving Notification Before Sending FCM Notification.");
        notificationService.saveNotification(content, targetUrl, member);
    }

    private Message createFCMMessage(String email, String content, String targetUrl) {
        return fcmNotificationService.createFCMMessage(email, content, targetUrl);
    }

    private void sendFCMNotification(String email, Message message)
            throws FirebaseMessagingException {
        log.info("Start Sending Synchronous FCM Notification. Thread: [{}]", currentThread().getName());
        fcmNotificationService.sendFCMNotification(email, message);
        log.info("End Synchronous FCM Notification Sending. Thread: [{}]", currentThread().getName());
    }
}
