package com.example.sabujak.image.exception;

import com.example.sabujak.common.exception.CustomException;
import com.example.sabujak.common.exception.ErrorCode;

public class ImageException extends CustomException {
    public ImageException(ErrorCode errorCode) {
        super(errorCode);
    }
}
