package com.example.sabujak.reservation.dto.event;

import java.time.LocalDateTime;

public interface ReserveEvent {
    Long reservationId();
    LocalDateTime reservationDate();
    String targetUrl();
    String reservationContent();
}
