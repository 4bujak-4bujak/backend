package com.example.sabujak.reservation.dto.event;

import com.example.sabujak.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

public record ReserveMeetingRoomEvent(
        Long reservationId,
        LocalDateTime reservationDate,
        String invitationContent,
        String reservationContent,
        List<Member> participants,
        List<CancelRechargingRoomNotification> cancelRechargingRoomNotifications
) implements ReserveEvent {
}
