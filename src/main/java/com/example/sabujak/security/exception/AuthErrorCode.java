package com.example.sabujak.security.exception;

import com.example.sabujak.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 가입된 이메일 입니다."),
    PHONE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 가입된 전화번호 입니다."),
    UNCONTRACTED_COMPANY(HttpStatus.BAD_REQUEST, "가입되지 않은 회사입니다."),
    EXPIRED_EMAIL_CODE(HttpStatus.BAD_REQUEST, "이메일 인증 코드가 만료되었습니다."),
    INVALID_EMAIL_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 인증 코드입니다."),
    EXPIRED_PHONE_CODE(HttpStatus.BAD_REQUEST, "핸드폰 인증 코드가 만료되었습니다."),
    INVALID_PHONE_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 핸드폰 인증 코드입니다."),
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "가입되지 않은 이메일이거나 비밀번호가 일치하지 않습니다."),
    ACCOUNT_DISABLED(HttpStatus.UNAUTHORIZED, "삭제된 계정입니다."),
    ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, "계정이 잠겨있습니다."),
    ACCOUNT_NOT_EXISTS(HttpStatus.BAD_REQUEST, "존재하지 않는 계정입니다");

    private final HttpStatus httpStatus;
    private final String message;
}