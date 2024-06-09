package com.example.sabujak.reservation.service;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.reservation.dto.event.ReserveEvent;
import com.example.sabujak.reservation.dto.event.ReserveMeetingRoomEvent;
import com.example.sabujak.reservation.dto.event.ReserveRechargingRoomEvent;
import com.example.sabujak.space.dto.SpaceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

import static com.example.sabujak.space.dto.SpaceType.MEETINGROOM;
import static com.example.sabujak.space.dto.SpaceType.RECHARGINGROOM;
import static java.lang.Thread.currentThread;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationNotificationScheduleEventListener {

    private final Clock clock;
    private final TaskScheduler taskScheduler;
    private final ReservationService reservationService;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Order(3)
    public void addMeetingRoomEntryNotificationSchedule(ReserveMeetingRoomEvent event) {
        addSchedule(event, MEETINGROOM);
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void addRechargingRoomEntryNotificationSchedule(ReserveRechargingRoomEvent event) {
        addSchedule(event, RECHARGINGROOM);
    }

    private void addSchedule(ReserveEvent event, SpaceType type) {
        Long reservationId = event.reservationId();
        LocalDateTime notificationTime = event.reservationDate().minusMinutes(30);
        log.info("Add Schedule Notification. " +
                "Reservation ID: [{}], Space Type: [{}], Notification Time: [{}]", reservationId, type, notificationTime);
        String targetUrl = event.targetUrl();
        String content = event.reservationContent();
        taskScheduler.schedule(() -> {
            log.info("Executing Scheduled Task. Current Scheduling Thread Name: [{}]", currentThread().getName());
            switch (type) {
                case MEETINGROOM -> reservationService.findMeetingRoomEntryNotificationMembers(reservationId, targetUrl, content);
                case RECHARGINGROOM -> {
                    if (event instanceof ReserveRechargingRoomEvent rechargingRoomEvent) {
                        Member member = rechargingRoomEvent.member();
                        reservationService.findRechargingRoomEntryNotificationMember(reservationId, targetUrl, content, member);
                    }
                }
            }
        }, toInstant(notificationTime));
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(clock.getZone()).toInstant();
    }
}
