package com.example.sabujak.reservation.repository;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.reservation.entity.Reservation;
import com.example.sabujak.space.entity.RechargingRoom;

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

    List<Reservation> findReservationsToday(Member member, LocalDateTime now);
    Integer countTodayReservation(Member member, LocalDateTime now);

    List<Reservation> findAllByRechargingRoomListAndStartTimes(List<RechargingRoom> rechargingRooms, LocalDateTime startAt, LocalDateTime endAt);

    boolean existsOverlappingRechargingRoomReservationByStartAt(Member member, LocalDateTime startAt);
    boolean existsOverlappingMeetingRoomReservationsByStartAt(Member member, LocalDateTime startAt);
}
