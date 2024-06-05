package com.example.sabujak.space.exception.meetingroom;

import com.example.sabujak.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum SpaceErrorCode implements ErrorCode {

    MEETING_ROOM_NOT_FOUND(NOT_FOUND, "8-001", "존재하지 않는 미팅룸입니다"),
    FOCUS_DESK_NOT_FOUND(NOT_FOUND, "8-002", "존재하지 않는 포커스 데스크(좌석) 입니다");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
