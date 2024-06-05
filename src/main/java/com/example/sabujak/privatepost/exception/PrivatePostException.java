package com.example.sabujak.privatepost.exception;

import com.example.sabujak.common.exception.CustomException;
import com.example.sabujak.common.exception.ErrorCode;

public class PrivatePostException extends CustomException {
    public PrivatePostException(ErrorCode errorCode) {
        super(errorCode);
    }
}
