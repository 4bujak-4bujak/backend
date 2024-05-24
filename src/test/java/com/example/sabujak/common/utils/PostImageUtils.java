package com.example.sabujak.common.utils;

import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.entity.PostImage;

public class PostImageUtils {

    private PostImageUtils() {
    }

    public static PostImage createPostImage(Post post) {
        return PostImage.builder()
                .path("https://userimage.bucket.s3.ap-northeast-2.amazonaws.com/d238b485-aimage.png")
                .build();
    }
}
