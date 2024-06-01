package com.example.sabujak.fcm.exception;

import com.example.sabujak.common.exception.CustomException;

public class FCMException extends CustomException {

    public FCMException(FCMErrorCode fcmErrorCode) {
        super(fcmErrorCode);
    }
}
