package com.example.sabujak.post.dto;

import com.example.sabujak.member.entity.Member;

public record saveCommentEvent(
        String targetUrl,
        String notificationContent,
        String receiverEmail,
        Member receiver
) {
}
