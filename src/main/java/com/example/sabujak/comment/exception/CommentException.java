package com.example.sabujak.comment.exception;

import com.example.sabujak.common.exception.CustomException;

public class CommentException extends CustomException {

    public CommentException(CommentErrorCode commentErrorCode) {
        super(commentErrorCode);
    }
}
