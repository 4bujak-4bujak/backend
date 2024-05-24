package com.example.sabujak.post.dto;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.post.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        String content,
        LocalDateTime createdDate,
        WriterResponse writer,
        boolean isWriter
) {
    public static CommentResponse of(Comment comment, Member member, boolean isWriter) {
        WriterResponse writer = WriterResponse.of(member);
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedDate(),
                writer,
                isWriter
        );
    }
}
