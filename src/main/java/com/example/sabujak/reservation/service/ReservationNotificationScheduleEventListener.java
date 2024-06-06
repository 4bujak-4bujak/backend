package com.example.sabujak.reservation.service;

import com.example.sabujak.reservation.dto.ReserveMeetingRoomEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationNotificationScheduleEventListener {

    private final Clock clock;
    private final TaskScheduler taskScheduler;
    private final ReservationService reservationService;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Order(value = LOWEST_PRECEDENCE)
    public void addMeetingRoomReservationNotificationSchedule(ReserveMeetingRoomEvent event) {
        Long reservationId = event.reservationId();
        LocalDateTime notificationTime = event.reservationDate().minusMinutes(30);
        log.info("Add Schedule Notification For Meeting Room Reservation. " +
                 "Reservation ID: [{}], Notification Time: [{}]", reservationId, notificationTime);
        String targetUrl = event.targetUrl();
        String content = event.reservationContent();
        taskScheduler.schedule(
                () -> reservationService.sendReservationNotification(reservationId, targetUrl, content),
                toInstant(notificationTime)
        );
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(clock.getZone()).toInstant();
    }
}