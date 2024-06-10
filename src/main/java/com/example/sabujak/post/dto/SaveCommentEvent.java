package com.example.sabujak.post.dto;

import com.example.sabujak.member.entity.Member;

public record SaveCommentEvent(
        Long postId,
        String notificationContent,
        String commenterImage,
        String writerEmail,
        Member writer
) {
}
