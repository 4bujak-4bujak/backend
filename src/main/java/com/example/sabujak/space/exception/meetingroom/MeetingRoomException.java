package com.example.sabujak.space.exception.meetingroom;

import com.example.sabujak.common.exception.CustomException;

public class MeetingRoomException extends CustomException {

    public MeetingRoomException(MeetingRoomErrorCode meetingRoomErrorCode) {
        super(meetingRoomErrorCode);
    }
}
