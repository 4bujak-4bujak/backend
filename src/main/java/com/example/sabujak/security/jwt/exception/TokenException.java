package com.example.sabujak.security.jwt.exception;

import com.example.sabujak.common.exception.CustomException;

public class TokenException extends CustomException {

    public TokenException(TokenErrorCode tokenErrorCode) {
        super(tokenErrorCode);
    }
}
