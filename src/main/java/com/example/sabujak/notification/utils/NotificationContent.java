package com.example.sabujak.notification.utils;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class NotificationContent {

    private static final String COMMENT_NOTIFICATION_FORMAT = "%s님이 회원님의 게시글에 댓글을 남겼습니다.";
    private static final String MEETING_ROOM_INVITATION_NOTIFICATION_FORMAT = "%s에 초대된 미팅이 있습니다.";
    private static final String RECHARGING_ROOM_CANCELLATION_NOTIFICATION_FORMAT = "%s에 리차징룸 예약이 미팅 일정과 중복되어 자동 취소되었습니다.";
    private static final String MEETING_ROOM_RESERVATION_NOTIFICATION_FORMAT = "%s에 %s %s에서 이용 예정입니다.";
    private static final String RECHARGING_ROOM__RESERVATION_NOTIFICATION_FORMAT = "%s에 %s %s 이용 예정입니다.";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM월 dd일 HH:mm");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static String createCommentContent(String nickName) {
        return String.format(COMMENT_NOTIFICATION_FORMAT, nickName);
    }

    public static String createMeetingRoomInvitationContent(LocalDateTime reservationDate) {
        return String.format(MEETING_ROOM_INVITATION_NOTIFICATION_FORMAT, formatDateTime(reservationDate));
    }

    public static String createRechargingRoomCancellationNotificationContent(LocalDateTime reservationDate) {
        return String.format(RECHARGING_ROOM_CANCELLATION_NOTIFICATION_FORMAT, formatTime(reservationDate));
    }

    public static String createMeetingRoomReservationContent(LocalDateTime reservationDate, String branchName, String spaceName) {
        return String.format(MEETING_ROOM_RESERVATION_NOTIFICATION_FORMAT, formatTime(reservationDate), branchName, spaceName);
    }

    public static String createRechargingRoomReservationContent(LocalDateTime reservationDate, String branchName, String spaceName) {
        return String.format(RECHARGING_ROOM__RESERVATION_NOTIFICATION_FORMAT, formatTime(reservationDate), branchName, spaceName);
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(dateTimeFormatter);
    }

    private static String formatTime(LocalDateTime dateTime) {
        return dateTime.format(timeFormatter);
    }
}
