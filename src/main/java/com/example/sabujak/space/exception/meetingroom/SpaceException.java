package com.example.sabujak.space.exception.meetingroom;

import com.example.sabujak.common.exception.CustomException;

public class SpaceException extends CustomException {

    public SpaceException(SpaceErrorCode spaceErrorCode) {
        super(spaceErrorCode);
    }
}
