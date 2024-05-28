package com.example.sabujak.post.dto;

import com.example.sabujak.member.entity.Member;

public record CommentCreatedEvent(
        Long postId,
        Member writer,
        String writerEmail,
        String commenterNickName
) {

    public static CommentCreatedEvent createCommentCreatedEvent(Long postId, Member writer, String commenterNickName) {
        return new CommentCreatedEvent(postId, writer, writer.getMemberEmail(), commenterNickName);
    }
}
