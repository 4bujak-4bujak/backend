package com.example.sabujak.comment.exception;

import com.example.sabujak.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    COMMENT_NOT_FOUND(BAD_REQUEST, "4-001", "존재하지 않는 댓글 입니다."),
    COMMENT_DELETE_DENIED(BAD_REQUEST, "4-002", "댓글 삭제 권한이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
