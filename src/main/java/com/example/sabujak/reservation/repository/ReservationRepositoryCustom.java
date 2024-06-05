package com.example.sabujak.reservation.repository;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.reservation.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepositoryCustom {

    boolean existsOverlappingMeetingRoomReservation(Member member, LocalDateTime startAt, LocalDateTime endAt);

    List<Reservation> findTodayFocusDeskReservationOrderByTime(Member member, LocalDateTime startAt);

    List<Reservation> findReservationsWithDuration(Member member, LocalDateTime now, int duration);
    Integer countTodayReservation(Member member, LocalDateTime now);
}
