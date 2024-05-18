package com.example.sabujak.common.utils;

import com.example.sabujak.post.dto.PostSaveRequest;
import com.example.sabujak.post.entity.Post;

import static com.example.sabujak.post.entity.Category.OWNER;
import static com.example.sabujak.post.entity.Tag.FREE;

public final class PostUtils {

    private static final String POST_TITLE = "글 제목";
    private static final String POST_CONTENT = "글 내용";

    private PostUtils() {
    }

    public static Post createPost() {
        return Post.builder()
                .category(OWNER)
                .tag(FREE)
                .title(POST_TITLE)
                .content(POST_CONTENT)
                .build();
    }

    public static PostSaveRequest createPostSaveRequest() {
        return new PostSaveRequest(OWNER, FREE, POST_TITLE, POST_CONTENT);
    }
}
