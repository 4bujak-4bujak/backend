package com.example.sabujak.reservation.dto;

import com.example.sabujak.member.entity.Member;

import java.util.List;

public record FindMeetingRoomEntryNotificationMembersEvent(
        String targetUrl,
        String content,
        List<Member> members
) {
}
