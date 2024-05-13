package com.example.sabujak.security.exception;

import com.example.sabujak.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "1-001", "이미 가입된 이메일 입니다."),
    PHONE_ALREADY_EXISTS(HttpStatus.CONFLICT, "1-002", "이미 가입된 전화번호 입니다."),
    UNCONTRACTED_COMPANY(HttpStatus.BAD_REQUEST, "1-003", "계약되지 않은 회사입니다."),
    EXPIRED_EMAIL_CODE(HttpStatus.BAD_REQUEST, "1-004", "이메일 인증 코드가 만료되었습니다."),
    INVALID_EMAIL_CODE(HttpStatus.BAD_REQUEST, "1-005", "유효하지 않은 이메일 인증 코드입니다."),
    EXPIRED_PHONE_CODE(HttpStatus.BAD_REQUEST, "1-006", "핸드폰 인증 코드가 만료되었습니다."),
    INVALID_PHONE_CODE(HttpStatus.BAD_REQUEST, "1-007", "유효하지 않은 핸드폰 인증 코드입니다."),
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "1-008", "가입되지 않은 이메일이거나 비밀번호가 일치하지 않습니다."),
    ACCOUNT_DISABLED(HttpStatus.UNAUTHORIZED, "1-009", "삭제된 계정입니다."),
    ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, "1-010", "계정이 잠겨있습니다."),
    ACCOUNT_NOT_EXISTS(HttpStatus.BAD_REQUEST, "1-011", "존재하지 않는 계정입니다."),
    UNKOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "1-012", "로그인 중 알 수 없는 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
