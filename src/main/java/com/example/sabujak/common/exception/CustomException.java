package com.example.sabujak.common.exception;

import lombok.Getter;

@Getter
public abstract class CustomException extends RuntimeException {

    public abstract ErrorCode getErrorCode();
}
