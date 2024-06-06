package com.example.sabujak.fcm.service;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.notification.entity.NotificationType;
import com.example.sabujak.notification.service.NotificationService;
import com.example.sabujak.post.dto.SaveCommentEvent;
import com.example.sabujak.reservation.dto.ReserveMeetingRoomEvent;
import com.example.sabujak.reservation.dto.SendReservationNotificationEvent;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.example.sabujak.fcm.constants.FCMConstants.*;
import static com.example.sabujak.notification.entity.NotificationType.COMMUNITY;
import static com.example.sabujak.notification.entity.NotificationType.RESERVATION;
import static java.lang.Thread.currentThread;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMNotificationEventListener {

    private final NotificationService notificationService;
    private final FCMNotificationService fcmNotificationService;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Async
    public void saveAndSendFCMNotificationForComment(SaveCommentEvent event) {
        String email = event.receiverEmail();
        String content = event.notificationContent();
        String targetUrl = event.targetUrl();
        log.info("Preparing FCM Notification For Comment. " +
                 "Receiver Email: [{}], Notification Content: [{}], Target URL: [{}]", email, content, targetUrl);

        saveNotification(DEFAULT_TITLE, content, targetUrl, COMMUNITY, event.receiver());
        sendFCMNotification(email, createFCMMessage(DEFAULT_TITLE, email, content, targetUrl));
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Order(value = HIGHEST_PRECEDENCE)
    @Async
    public void saveAndSendFCMNotificationForMeetingRoomInvitation(ReserveMeetingRoomEvent event) {
        String content = event.invitationContent();
        String targetUrl = event.targetUrl();
        log.info("Preparing FCM Notification For Meeting Room Invitation. " +
                 "Notification Content: [{}], Target URL: [{}]", content, targetUrl);

        for (Member participant : event.participants()) {
            String email = participant.getMemberEmail();
            log.info("Participant Email: [{}]", email);

            saveNotification(MEETING_ROOM_INVITATION_TITLE, content, targetUrl, RESERVATION, participant);
            sendFCMNotificationAsync(email, createFCMMessage(MEETING_ROOM_INVITATION_TITLE, email, content, targetUrl));
        }
    }

    @EventListener
    public void saveAndSendFCMNotificationForMeetingRoomReservation(SendReservationNotificationEvent event) {
        String content = event.content();
        String targetUrl = event.targetUrl();
        log.info("Preparing FCM Notification For Meeting Room Reservation. " +
                 "Notification Content: [{}], Target URL: [{}]", content, targetUrl);

        for (Member receiver : event.receivers()) {
            String email = receiver.getMemberEmail();
            log.info("Receiver Email: [{}]", email);

            saveNotification(MEETING_ROOM_RESERVATION_TITLE, content, targetUrl, RESERVATION, receiver);
            sendFCMNotificationAsync(email, createFCMMessage(MEETING_ROOM_RESERVATION_TITLE, email, content, targetUrl));
        }
    }

    private void saveNotification(String title, String content, String targetUrl, NotificationType type, Member member) {
        log.info("Start Saving Notification Before Sending FCM Notification.");
        notificationService.saveNotification(title, content, targetUrl, type, member);
    }

    private Message createFCMMessage(String title, String email, String content, String targetUrl) {
        return fcmNotificationService.createFCMMessage(title, email, content, targetUrl);
    }

    private void sendFCMNotification(String email, Message message) {
        fcmNotificationService.sendFCMNotification(email, message);
    }

    private void sendFCMNotificationAsync(String email, Message message) {
        log.info("Start Sending Asynchronous FCM Notification. " +
                "Current Thread Name: [{}]", currentThread().getName());
        fcmNotificationService.sendFCMNotificationAsync(email, message);
        log.info("End Asynchronous FCM Notification Sending. " +
                "Current Thread Name: [{}]", currentThread().getName());
    }
}
