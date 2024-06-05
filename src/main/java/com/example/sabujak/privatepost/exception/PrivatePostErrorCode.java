package com.example.sabujak.privatepost.exception;

import com.example.sabujak.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum PrivatePostErrorCode implements ErrorCode {
    PRIVATEPOST_NOT_FOUND(NOT_FOUND, "10-001", "존재하지 않는 일대일 문의 글 입니다."),
    PRIVATEPOST_GET_DENIED(BAD_REQUEST, "10-002", "작성자가 아니라 글 내용을 볼 수 없습니다."),
    PRIVATEPOST_DELETE_DENIED(BAD_REQUEST, "10-003", "작성자가 아니라 글을 삭제할 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
