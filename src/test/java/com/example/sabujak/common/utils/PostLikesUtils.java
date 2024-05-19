package com.example.sabujak.common.utils;

import com.example.sabujak.post.entity.PostLike;
import com.example.sabujak.post.entity.PostLikeId;

public class PostLikesUtils {

    private PostLikesUtils() {
    }

    public static PostLikeId createPostLikeId(Long postId, String memberEmail) {
        return PostLikeId.builder()
                .postId(postId)
                .memberEmail(memberEmail)
                .build();
    }

    public static PostLike createPostLike(PostLikeId postLikeId) {
        return PostLike.builder()
                .id(postLikeId)
                .build();
    }
}
