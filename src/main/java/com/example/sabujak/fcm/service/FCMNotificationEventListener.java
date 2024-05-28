package com.example.sabujak.fcm.service;

import com.example.sabujak.notification.entity.NotificationContent;
import com.example.sabujak.notification.service.NotificationService;
import com.example.sabujak.post.dto.CommentCreatedEvent;
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

    private final FCMNotificationService fcmNotificationService;
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Async
    public void sendCommentFCMNotification(CommentCreatedEvent event) {
        String targetUrl = COMMUNITY_NOTIFICATION_URL_PREFIX + event.postId();
        log.info("Sending FCM Notification For Comment. Target URL: [{}]", targetUrl);

        String receiverEmail = event.writerEmail();
        String senderNickname = event.commenterNickName();
        log.info("Receiver Email: [{}], Sender Nickname: [{}]", receiverEmail, senderNickname);

        String content = createContent(senderNickname, COMMENT);
        log.info("Notification Content: [{}]", content);

        String notificationId = notificationService.saveNotification(targetUrl, content, event.writer()).toString();
        fcmNotificationService.sendFCMNotification(receiverEmail, content, notificationId, targetUrl);
    }

    private String createContent(String nickname, NotificationContent content) {
        return nickname.concat(content.getSuffix());
    }
}
