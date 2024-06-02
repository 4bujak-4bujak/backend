package com.example.sabujak.reservation.exception;

import com.example.sabujak.common.exception.CustomException;

public class ReservationException extends CustomException {

    public ReservationException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode);
    }
}
