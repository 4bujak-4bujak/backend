package com.example.sabujak.post.exception;

import com.example.sabujak.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {

    POST_NOT_FOUND(BAD_REQUEST, "3-001", "존재하지 않는 게시글 입니다."),
    POST_DELETE_DENIED(BAD_REQUEST, "3-002", "게시글 삭제 권한이 존재하지 않습니다."),
    POST_LIKE_NOT_FOUND(BAD_REQUEST, "3-003", "존재하지 않는 관심 글 입니다."),
    COMMENT_NOT_FOUND(BAD_REQUEST, "3-004", "존재하지 않는 댓글 입니다."),
    COMMENT_DELETE_DENIED(BAD_REQUEST, "3-005", "댓글 삭제 권한이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
