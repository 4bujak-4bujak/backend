package com.example.sabujak.reservation.dto.event;

import com.example.sabujak.member.entity.Member;

public record CancelRechargeRoomNotification(
        Member member,
        String cancellationContent
) {
}
