package com.example.sabujak.post.dto;

import com.example.sabujak.member.entity.Member;

public record CommentCreatedEvent(
        String targetUrl,
        String notificationContent,
        String receiverEmail,
        Member receiver
) {
    public static CommentCreatedEvent createCommentCreatedEvent(
            String targetUrl,
            String notificationContent,
            String receiverEmail,
            Member receiver
    ) {
        return new CommentCreatedEvent(
                targetUrl,
                notificationContent,
                receiverEmail,
                receiver
        );
    }
}
