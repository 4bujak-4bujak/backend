package com.example.sabujak.security.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {
    @Getter
    private String customErrorCode;

    public CustomAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CustomAuthenticationException(String msg) {
        super(msg);
    }
    public CustomAuthenticationException(String msg, String customErrorCode) {
        super(msg);
        this.customErrorCode = customErrorCode;
    }
}
