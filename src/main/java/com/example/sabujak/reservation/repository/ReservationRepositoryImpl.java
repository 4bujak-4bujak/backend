package com.example.sabujak.reservation.repository;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.reservation.entity.Reservation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.example.sabujak.reservation.entity.QMemberReservation.memberReservation;
import static com.example.sabujak.reservation.entity.QReservation.reservation;
import static com.example.sabujak.space.entity.QSpace.space;

@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

//    @Override
//    public boolean existsOverlappingMeetingRoomReservation(Member member, LocalDateTime startAt, LocalDateTime endAt) {
//        return queryFactory.selectOne()
//                .from(reservation)
//                .join(reservation.memberReservations, memberReservation)
//                .join(reservation.space, space)
//                .where(memberReservation.member.eq(member),
//                        space.dtype.eq("MeetingRoom"),
//                        reservation.reservationStartDateTime.between(startAt, endAt)
//                                .or(reservation.reservationEndDateTime.between(startAt, endAt))
//                                .or(reservation.reservationStartDateTime.before(startAt)
//                                        .and(reservation.reservationEndDateTime.after(endAt))))
//                .fetchFirst() != null;
//    }

    @Override
    public List<Reservation> findOverlappingFocusDeskReservation(Member member, LocalDateTime startAt) {
        LocalDateTime endAt = startAt;
        startAt = startAt.with(LocalTime.MIDNIGHT);
        return queryFactory.selectFrom(reservation)
                .join(reservation.memberReservations, memberReservation).fetchJoin()
                .join(reservation.space, space).fetchJoin()
                .where(memberReservation.member.eq(member),
                        space.dtype.eq("FocusDesk"),
                        reservation.reservationStartDateTime.between(startAt, endAt))
                .orderBy(reservation.reservationId.asc())
                .fetch();
    }
}
