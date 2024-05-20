package com.example.sabujak.post.dto;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.post.entity.Comment;
import com.example.sabujak.post.entity.Post;

public record SaveCommentRequest(String content) {
    public Comment toEntity(Member member, Post post) {
        Comment comment = Comment.builder()
                .content(content)
                .build();
        comment.setMember(member);
        comment.setPost(post);
        return comment;
    }
}
