package com.example.sabujak.post.dto;

import com.example.sabujak.member.entity.Member;

public record PostEvent(
        String targetUrl,
        String notificationContent,
        String receiverEmail,
        Member receiver
) {
    public static PostEvent createPostEvent(
            String targetUrl,
            String notificationContent,
            String receiverEmail,
            Member receiver
    ) {
        return new PostEvent(
                targetUrl,
                notificationContent,
                receiverEmail,
                receiver
        );
    }
}
