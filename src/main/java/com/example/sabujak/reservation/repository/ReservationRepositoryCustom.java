package com.example.sabujak.reservation.repository;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.reservation.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepositoryCustom {

    boolean existsOverlappingMeetingRoomReservation(Member member, LocalDateTime startAt, LocalDateTime endAt);
    boolean existsOverlappingMeetingRoomReservationInMembers(List<Member> members, LocalDateTime startAt, LocalDateTime endAt);

    boolean existsOverlappingRechargingRoomReservation(Member member, LocalDateTime startAt, LocalDateTime endAt);
    List<Reservation> findOverlappingRechargingRoomReservation(Member member, LocalDateTime startAt, LocalDateTime endAt);
    List<Reservation> findOverlappingRechargingRoomReservationInMembers(List<Member> members, LocalDateTime startAt, LocalDateTime endAt);



    List<Reservation> findTodayFocusDeskReservationOrderByTime(Member member, LocalDateTime startAt);

    List<Reservation> findReservationsWithDuration(Member member, LocalDateTime now, int durationStart, int durationEnd);

    Integer countTodayReservation(Member member, LocalDateTime now);
}
