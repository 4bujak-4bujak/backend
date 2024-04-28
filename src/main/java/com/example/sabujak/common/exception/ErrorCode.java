package com.example.sabujak.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    JSON_PROCESSING_ERROR(BAD_REQUEST, "JSON 처리 중 문제가 발생했습니다. 데이터 형식이 잘못되었거나 유효하지 않은 JSON 형식입니다.");

    private final HttpStatus status;
    private final String message;
}
