package com.example.sabujak.reservation.exception;

import com.example.sabujak.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements ErrorCode {

    REPRESENTATIVE_OVERLAPPING_MEETINGROOM_EXISTS(BAD_REQUEST, "9-001", "예약 당사자의 겹치는 미팅룸 예약이 존재합니다."),
    ALREADY_RESERVED_FOCUS_DESK(BAD_REQUEST, "9-002", "이미 예약된 포커스 데스크 좌석입니다.");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
