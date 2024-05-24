package com.example.sabujak.common.utils;

import com.example.sabujak.post.dto.SaveCommentRequest;
import com.example.sabujak.post.entity.Comment;

public class CommentUtils {

    private static final String COMMENT_CONTENT = "댓글 내용";

    private CommentUtils() {
    }

    public static Comment createComment() {
        return Comment.builder()
                .content(COMMENT_CONTENT)
                .build();
    }

    public static SaveCommentRequest createSaveCommentRequest() {
        return new SaveCommentRequest(COMMENT_CONTENT);
    }
}
