package com.example.sabujak.image.exception;

import com.example.sabujak.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {

    EMPTY_IMAGE_FILE(HttpStatus.BAD_REQUEST, "5-001", "이미지가 빈 파일 입니다."),
    NO_EXTENSION_IMAGE_FILE(HttpStatus.BAD_REQUEST, "5-002", "파일의 확장자가 없습니다"),
    INVALID_EXTENSION_IMAGE_FILE(HttpStatus.BAD_REQUEST, "5-003", "이미지에 해당하는 확장자가 아닙니다"),
    FAILED_UPLOADING_IMAGE_FILE(HttpStatus.FORBIDDEN, "5-004", "이미지를 s3에 업로드 하는데 실패했습니다."),
    FAILED_DELETING_IMAGE_FILE(HttpStatus.FORBIDDEN, "5-005", "이미지를 s3에서 삭제 하는데 실패했습니다."),
    NOT_FOUND_IMAGE_BY_URL(HttpStatus.NOT_FOUND, "5-006", "url에 해당하는 이미지가 없습니다.");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
