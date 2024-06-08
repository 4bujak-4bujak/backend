package com.example.sabujak.notification.utils;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class NotificationContent {

    private static final String COMMENT_NOTIFICATION_FORMAT = "%s에 %s님이 댓글을 남겼습니다.";
    private static final String MEETING_ROOM_INVITATION_NOTIFICATION_FORMAT = "%s에 초대된 미팅이 있습니다.";
    private static final String RECHARGING_ROOM_CANCELLATION_NOTIFICATION_FORMAT = "%s에 리차징룸 예약이 해당 시간 미팅으로 인해 취소되었습니다.";
    private static final String MEETING_ROOM_RESERVATION_NOTIFICATION_FORMAT = "%s에 %s %s 이용 예정입니다.";
    private static final String RECHARGING_ROOM__RESERVATION_NOTIFICATION_FORMAT = "%s에 %s에서 리차징룸 이용 예정입니다.";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일 HH:mm");

    public static String createCommentContent(String title, String nickName) {
        return String.format(COMMENT_NOTIFICATION_FORMAT, title, nickName);
    }

    public static String createMeetingRoomInvitationContent(LocalDateTime reservationDate) {
        return String.format(MEETING_ROOM_INVITATION_NOTIFICATION_FORMAT, formatDateTime(reservationDate));
    }

    public static String createRechargingRoomCancellationNotificationContent(LocalDateTime reservationDate) {
        return String.format(RECHARGING_ROOM_CANCELLATION_NOTIFICATION_FORMAT, formatDateTime(reservationDate));
    }

    public static String createMeetingRoomReservationContent(LocalDateTime reservationDate, String branchName, String spaceName) {
        return String.format(MEETING_ROOM_RESERVATION_NOTIFICATION_FORMAT, formatDateTime(reservationDate), branchName, spaceName);
    }

    public static String createRechargingRoomReservationContent(LocalDateTime reservationDate, String branchName) {
        return String.format(RECHARGING_ROOM__RESERVATION_NOTIFICATION_FORMAT, formatDateTime(reservationDate), branchName);
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }
}
