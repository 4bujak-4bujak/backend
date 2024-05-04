package com.example.sabujak.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommonException extends CustomException {
    private final CommonErrorCode commonErrorCode;

    @Override
    public ErrorCode getErrorCode() {
        return commonErrorCode;
    }
}
