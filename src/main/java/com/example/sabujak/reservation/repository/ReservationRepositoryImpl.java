package com.example.sabujak.reservation.repository;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.reservation.entity.Reservation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.example.sabujak.branch.entity.QBranch.branch;
import static com.example.sabujak.reservation.entity.QMemberReservation.memberReservation;
import static com.example.sabujak.reservation.entity.QReservation.reservation;
import static com.example.sabujak.space.entity.QSpace.space;

@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsOverlappingMeetingRoomReservation(Member member, LocalDateTime startAt, LocalDateTime endAt) {
        return queryFactory.selectOne()
                .from(reservation)
                .join(reservation.memberReservations, memberReservation)
                .join(reservation.space, space)
                .where(memberReservation.member.eq(member),
                        space.dtype.eq("MeetingRoom"),
                        reservation.reservationStartDateTime.between(startAt, endAt.minusNanos(1))
                                .or(reservation.reservationEndDateTime.between(startAt.plusNanos(1), endAt))
                                .or(reservation.reservationStartDateTime.before(startAt)
                                        .and(reservation.reservationEndDateTime.after(endAt))))
                .fetchFirst() != null;
    }

    @Override
    public boolean existsOverlappingRechargingRoomReservation(Member member, LocalDateTime startAt, LocalDateTime endAt) {
        return queryFactory.selectOne()
                .from(reservation)
                .join(reservation.memberReservations, memberReservation)
                .join(reservation.space, space)
                .where(memberReservation.member.eq(member),
                        space.dtype.eq("RechargingRoom"),
                        reservation.reservationStartDateTime.between(startAt, endAt.minusNanos(1))
                                .or(reservation.reservationEndDateTime.between(startAt.plusNanos(1), endAt))
                                .or(reservation.reservationStartDateTime.before(startAt)
                                        .and(reservation.reservationEndDateTime.after(endAt))))
                .fetchFirst() != null;
    }

    @Override
    public List<Reservation> findTodayFocusDeskReservationOrderByTime(Member member, LocalDateTime startAt) {
        LocalDateTime endAt = startAt;
        startAt = startAt.with(LocalTime.MIDNIGHT);
        return queryFactory.selectFrom(reservation)
                .join(reservation.memberReservations, memberReservation).fetchJoin()
                .join(reservation.space, space).fetchJoin()
                .where(memberReservation.member.eq(member),
                        space.dtype.eq("FocusDesk"),
                        reservation.reservationStartDateTime.between(startAt, endAt))
                .orderBy(reservation.reservationId.asc())
                //포커스 데스크는 시간을 설정하는게 아니라 예약한 시간부터 바로 사용하는거라 reservationId 가 곧 시간순
                .fetch();
    }

    @Override
    public List<Reservation> findReservationsWithDuration(Member member, LocalDateTime now, int durationStart, int durationEnd) {
        LocalDateTime startAt = now.plusDays(durationStart).with(LocalTime.MIDNIGHT);
        LocalDateTime endAt = now.plusDays(durationEnd).with(LocalTime.MAX);

        return queryFactory.selectFrom(reservation)
                .join(reservation.memberReservations, memberReservation)
                .join(reservation.space, space).fetchJoin()
                .join(space.branch, branch).fetchJoin()
                .where(memberReservation.member.eq(member),
                        reservation.reservationStartDateTime.between(startAt, endAt))
                .orderBy(reservation.reservationStartDateTime.asc())
                .fetch();
    }

    @Override
    public Integer countTodayReservation(Member member, LocalDateTime now) {
        LocalDateTime startAt = now.with(LocalTime.MIDNIGHT);
        LocalDateTime endAt = now.with(LocalTime.MAX);

        return Math.toIntExact(queryFactory.select(reservation.count())
                .from(reservation)
                .join(reservation.memberReservations, memberReservation)
                .join(reservation.space, space)
                .where(memberReservation.member.eq(member),
                        reservation.reservationStartDateTime.between(startAt, endAt))
                .fetchFirst());
    }
}
