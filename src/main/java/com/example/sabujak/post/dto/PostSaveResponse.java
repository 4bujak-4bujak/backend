package com.example.sabujak.post.dto;

import com.example.sabujak.post.entity.Post;
import lombok.Builder;

@Builder
public record PostSaveResponse(
        Long postId
) {
    public static PostSaveResponse of(Post post) {
        return PostSaveResponse.builder()
                .postId(post.getId())
                .build();
    }
}
