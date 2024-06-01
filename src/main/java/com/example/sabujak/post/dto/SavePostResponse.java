package com.example.sabujak.post.dto;

import com.example.sabujak.post.entity.Post;

public record SavePostResponse(Long postId) {
    public static SavePostResponse of(Post post) {
        return new SavePostResponse(post.getId());
    }
}
