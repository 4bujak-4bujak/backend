package com.example.sabujak.reservation.dto.event;

import com.example.sabujak.member.entity.Member;

import java.time.LocalDateTime;

public record ReserveRechargingRoomEvent(
        Long reservationId,
        LocalDateTime reservationDate,
        String reservationContent,
        Member member
) implements ReserveEvent {
}
