package com.example.sabujak.fcm.service;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.notification.entity.NotificationType;
import com.example.sabujak.notification.service.NotificationService;
import com.example.sabujak.post.dto.SaveCommentEvent;
import com.example.sabujak.reservation.dto.event.CancelRechargingRoomNotification;
import com.example.sabujak.reservation.dto.event.FindRechargingRoomEntryNotificationMemberEvent;
import com.example.sabujak.reservation.dto.event.ReserveMeetingRoomEvent;
import com.example.sabujak.reservation.dto.event.FindMeetingRoomEntryNotificationMembersEvent;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

import static com.example.sabujak.fcm.constants.FCMConstants.*;
import static com.example.sabujak.notification.entity.NotificationType.COMMUNITY;
import static com.example.sabujak.notification.entity.NotificationType.RESERVATION;
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
    public void saveAndSendFCMNotificationForComment(SaveCommentEvent event) {
        String email = event.writerEmail();
        String content = event.notificationContent();
        Long targetId = event.postId();
        log.info("Start Preparing FCM Notification For Comment. " +
                "Writer Email: [{}], Notification Content: [{}], Target URL: [{}]", email, content, targetId);

        saveNotification(event.commenterImage(), DEFAULT_TITLE, content, targetId, COMMUNITY, event.writer());
        sendFCMNotification(email, createFCMMessage(email, DEFAULT_TITLE, content, targetId));
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Order(1)
    @Async
    public void saveAndSendFCMNotificationForMeetingRoomInvitation(ReserveMeetingRoomEvent event) {
        List<Member> participants = event.participants();
        if (participants.isEmpty()) {
            return;
        }

        String content = event.invitationContent();
        Long targetId = event.reservationId();
        log.info("Start Preparing FCM Notification For Meeting Room Invitation. " +
                "Notification Content: [{}], Target ID: [{}]", content, targetId);

        for (Member participant : participants) {
            String email = participant.getMemberEmail();
            log.info("Notification Target Participant Email: [{}]", email);

            saveNotification(CHECK_IMAGE, MEETING_ROOM_INVITATION_TITLE, content, targetId, RESERVATION, participant);
            sendFCMNotificationAsync(email, createFCMMessage(email, MEETING_ROOM_INVITATION_TITLE, content, targetId));
        }
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Order(2)
    @Async
    public void saveAndSendFCMNotificationForRechargingRoomCancellation(ReserveMeetingRoomEvent event) {
        List<CancelRechargingRoomNotification> cancelRechargingRoomNotifications = event.cancelRechargingRoomNotifications();
        if (cancelRechargingRoomNotifications.isEmpty()) {
            return;
        }

        Long targetId = event.reservationId();
        log.info("Start Preparing FCM Notification For Recharging Room Cancellation. Target ID: [{}]", targetId);

        for (CancelRechargingRoomNotification cancelRechargingRoomNotification : cancelRechargingRoomNotifications) {
            Member canceler = cancelRechargingRoomNotification.member();
            String email = canceler.getMemberEmail();
            String content = cancelRechargingRoomNotification.cancellationContent();
            log.info("Notification Content: [{}], Notification Target Canceler Email: [{}]", content, email);

            saveNotification(CHECK_IMAGE, RECHARGING_ROOM_CANCELLATION_TITLE, content, targetId, RESERVATION, canceler);
            sendFCMNotificationAsync(email, createFCMMessage(email, RECHARGING_ROOM_CANCELLATION_TITLE, content, targetId));
        }
    }

    @EventListener
    public void saveAndSendFCMNotificationForMeetingRoomEntry(FindMeetingRoomEntryNotificationMembersEvent event) {
        String content = event.reservationContent();
        Long targetId = event.reservationId();
        log.info("Start Preparing FCM Notification For Meeting Room Entry. " +
                "Notification Content: [{}], Target ID: [{}]", content, targetId);

        for (Member member : event.members()) {
            String email = member.getMemberEmail();
            log.info("Notification Target Member Email: [{}]", email);

            saveNotification(WARING_IMAGE, MEETING_ROOM_RESERVATION_TITLE, content, targetId, RESERVATION, member);
            sendFCMNotificationAsync(email, createFCMMessage(email, MEETING_ROOM_RESERVATION_TITLE, content, targetId));
        }
    }

    @EventListener
    public void saveAndSendFCMNotificationForRechargingRoomEntry(FindRechargingRoomEntryNotificationMemberEvent event) {
        String email = event.memberEmail();
        String content = event.reservationContent();
        Long targetId = event.reservationId();
        log.info("Start Preparing FCM Notification For Recharging Room Entry. " +
                "Member Email: [{}], Notification Content: [{}], Target ID: [{}]", email, content, targetId);

        saveNotification(WARING_IMAGE, RECHARGING_ROOM_RESERVATION_TITLE, content, targetId, RESERVATION, event.member());
        sendFCMNotification(email, createFCMMessage(email, RECHARGING_ROOM_RESERVATION_TITLE, content, targetId));
    }

    private void saveNotification(String image, String title, String content, Long targetId, NotificationType type, Member member) {
        log.info("Start Saving Notification Before Sending FCM Notification.");
        notificationService.saveNotification(image, title, content, targetId, type, member);
    }

    private Message createFCMMessage(String email, String title, String content, Long targetId) {
        return fcmNotificationService.createFCMMessage(email, title, content, targetId);
    }

    private void sendFCMNotification(String email, Message message) {
        fcmNotificationService.sendFCMNotification(email, message);
    }

    private void sendFCMNotificationAsync(String email, Message message) {
        log.info("Start Sending Asynchronous FCM Notification. " +
                "Current Async Thread Name: [{}]", currentThread().getName());
        fcmNotificationService.sendFCMNotificationAsync(email, message);
        log.info("End Asynchronous FCM Notification Sending. " +
                "Current Async Thread Name: [{}]", currentThread().getName());
    }
}
