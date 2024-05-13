package com.example.sabujak.security.jwt.exception;

import com.example.sabujak.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum TokenErrorCode implements ErrorCode {

    ACCESS_TOKEN_NOT_EXIST(HttpStatus.BAD_REQUEST, "2-001", "ACCESS TOKEN이 존재하지 않습니다"),
    REFRESH_TOKEN_NOT_EXIST(HttpStatus.BAD_REQUEST, "2-002", "REFRESH TOKEN이 존재하지 않습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "2-003", "잘못된 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "2-004", "만료된 토큰입니다.");

    private final HttpStatus httpStatus;
    private final String message;
    private final String customCode;

}
