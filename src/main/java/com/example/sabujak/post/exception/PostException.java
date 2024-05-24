package com.example.sabujak.post.exception;

import com.example.sabujak.common.exception.CustomException;

public class PostException extends CustomException {

    public PostException(PostErrorCode postErrorCode) {
        super(postErrorCode);
    }
}
