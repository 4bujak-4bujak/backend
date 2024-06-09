package com.example.sabujak.reservation.dto.event;

import com.example.sabujak.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

public record ReserveMeetingRoomEvent(
        Long reservationId,
        LocalDateTime reservationDate,
        String targetUrl,
        String invitationContent,
        String reservationContent,
        List<Member> participants,
        List<CancelRechargeRoomNotification> cancelRechargeRoomNotifications
) implements ReserveEvent {
}
