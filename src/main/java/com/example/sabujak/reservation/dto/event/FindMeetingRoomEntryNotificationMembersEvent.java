package com.example.sabujak.reservation.dto.event;

import com.example.sabujak.member.entity.Member;

import java.util.List;

public record FindMeetingRoomEntryNotificationMembersEvent(
        Long reservationId,
        String reservationContent,
        List<Member> members
) {
}
