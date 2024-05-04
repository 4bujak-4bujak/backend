package com.example.sabujak.common.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    COMMON_SYSTEM_ERROR(INTERNAL_SERVER_ERROR, "서버에 내부 오류가 발생했습니다. 요청을 처리하는 동안 예상치 못한 문제가 발생했습니다."),
    COMMON_JSON_PROCESSING_ERROR(BAD_REQUEST, "JSON 처리 중 문제가 발생했습니다. 데이터 형식이 잘못되었거나 유효하지 않은 JSON 형식입니다."),
    COMMON_RESOURCE_NOT_FOUND(NOT_FOUND, "요청한 리소스를 찾을 수 없습니다. 요청한 URL에 해당하는 리소스가 없거나 삭제되었을 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
