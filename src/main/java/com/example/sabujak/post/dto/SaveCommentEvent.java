package com.example.sabujak.post.dto;

import com.example.sabujak.member.entity.Member;

public record SaveCommentEvent(
        String targetUrl,
        String notificationContent,
        String receiverEmail,
        Member recipient
) {
}
