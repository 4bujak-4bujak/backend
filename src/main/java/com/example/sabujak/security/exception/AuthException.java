package com.example.sabujak.security.exception;

import com.example.sabujak.common.exception.CustomException;

public class AuthException extends CustomException {

    public AuthException(AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }
}