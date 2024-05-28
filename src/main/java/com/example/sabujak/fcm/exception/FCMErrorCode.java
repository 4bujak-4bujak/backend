package com.example.sabujak.fcm.exception;

import com.example.sabujak.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@RequiredArgsConstructor
public enum FCMErrorCode implements ErrorCode {

    FCM_TOKEN_NOT_FOUND(BAD_REQUEST, "6-001", "FCM Token이 존재하지 않습니다."),
    FCM_SENDING_MESSAGE_FAILED(INTERNAL_SERVER_ERROR, "6-002", "FCM 알림 전송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
