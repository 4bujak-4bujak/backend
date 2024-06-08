package com.example.sabujak.reservation.dto.event;

import com.example.sabujak.member.entity.Member;

public record FindRechargingRoomEntryNotificationMemberEvent(
        String targetUrl,
        String content,
        String memberEmail,
        Member member
) {
}
