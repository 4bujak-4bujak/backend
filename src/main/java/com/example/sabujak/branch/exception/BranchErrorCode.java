package com.example.sabujak.branch.exception;

import com.example.sabujak.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BranchErrorCode implements ErrorCode {

    ENTITY_NOT_FOUND_BY_NAME(HttpStatus.NOT_FOUND, "4-001", "이름에 해당하는 지점이 존재하지 않습니다"),
    BRANCH_NOT_FOUND(HttpStatus.NOT_FOUND, "4-002", "지점이 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
