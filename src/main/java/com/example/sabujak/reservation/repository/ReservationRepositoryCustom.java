package com.example.sabujak.reservation.repository;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.reservation.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepositoryCustom {

    boolean existsOverlappingMeetingRoomReservation(Member member, LocalDateTime startAt, LocalDateTime endAt);

    List<Reservation> findTodayFocusDeskReservationOrderByTime(Member member, LocalDateTime startAt);

    List<Reservation> findTodayReservations(Member member, LocalDateTime now);
    Integer countTodayReservation(Member member, LocalDateTime now);
}
