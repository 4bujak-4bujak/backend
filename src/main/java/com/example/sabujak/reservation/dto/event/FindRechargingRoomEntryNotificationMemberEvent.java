package com.example.sabujak.reservation.dto.event;

import com.example.sabujak.member.entity.Member;

public record FindRechargingRoomEntryNotificationMemberEvent(
        Long reservationId,
        String reservationContent,
        String memberEmail,
        Member member
) {
}
