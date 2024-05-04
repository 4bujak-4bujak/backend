package com.example.sabujak.common.exception;

public class CommonException extends CustomException {

    public CommonException(CommonErrorCode commonErrorCode) {
        super(commonErrorCode);
    }
}
