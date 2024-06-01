package com.example.sabujak.fcm.exception;

import com.example.sabujak.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum FCMErrorCode implements ErrorCode {

    FCM_TOKEN_NOT_FOUND(BAD_REQUEST, "6-001", "FCM 토큰이 존재하지 않습니다."),
    INVALID_FCM_TOKEN(BAD_REQUEST, "6-002", "FCM 토큰이 유효하지 않습니다."),
    FCM_SERVER_ERROR(INTERNAL_SERVER_ERROR, "6-003", "FCM 서버의 오류로 메시지 전송에 실패했습니다."),
    FCM_UNAUTHORIZED(UNAUTHORIZED, "6-004", "FCM 인증 오류로 메시지 전송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
