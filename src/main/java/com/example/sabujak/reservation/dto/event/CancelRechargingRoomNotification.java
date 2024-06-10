package com.example.sabujak.reservation.dto.event;

import com.example.sabujak.member.entity.Member;

public record CancelRechargingRoomNotification(
        String cancellationContent,
        Member member
) {
}
