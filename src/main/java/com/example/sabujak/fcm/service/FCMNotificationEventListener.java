package com.example.sabujak.fcm.service;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.notification.entity.NotificationContent;
import com.example.sabujak.notification.service.NotificationService;
import com.example.sabujak.post.dto.CommentCreatedEvent;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.example.sabujak.fcm.constants.FCMConstants.COMMUNITY_NOTIFICATION_URL_PREFIX;
import static com.example.sabujak.notification.entity.NotificationContent.COMMENT;
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
        String targetUrl = COMMUNITY_NOTIFICATION_URL_PREFIX + event.postId();
        log.info("Preparing FCM Notification For Comment. Target URL: [{}]", targetUrl);

        String email = event.writerEmail();
        String nickName = event.commenterNickName();
        log.info("Receiver Email: [{}], Sender Nickname: [{}]", email, nickName);

        String content = createContent(nickName, COMMENT);
        log.info("Notification Content: [{}]", content);

        log.info("Saving Notification For Comment...");
        Long notificationId = saveNotification(content, targetUrl, event.writer());

        log.info("Sending FCM Notification For Comment...");
        sendFCMNotification(email, createFCMMessage(email, content, notificationId, targetUrl));
    }

    private String createContent(String nickname, NotificationContent content) {
        return nickname + content.getSuffix();
    }

    private Long saveNotification(String content, String targetUrl, Member member) {
        return notificationService.saveNotification(content, targetUrl, member);
    }

    private Message createFCMMessage(String email, String content, Long notificationId, String targetUrl) {
        return fcmNotificationService.createFCMMessage(email, content, notificationId, targetUrl);
    }

    private void sendFCMNotification(String email, Message message)
            throws FirebaseMessagingException {
        fcmNotificationService.sendFCMNotification(email, message);
    }
}
