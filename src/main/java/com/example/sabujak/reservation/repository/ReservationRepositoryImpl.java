package com.example.sabujak.reservation.repository;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.reservation.entity.Reservation;
import com.example.sabujak.reservation.entity.ReservationStatus;
import com.example.sabujak.space.entity.RechargingRoom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
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
                        memberReservation.memberReservationStatus.eq(ReservationStatus.ACCEPTED),
                        space.dtype.eq("MeetingRoom"),
                        reservationCondition(startAt, endAt))
                .fetchFirst() != null;
    }

    @Override
    public boolean existsOverlappingMeetingRoomReservationInMembers(List<Member> members, LocalDateTime startAt, LocalDateTime endAt) {
        return queryFactory.selectOne()
                .from(reservation)
                .join(reservation.memberReservations, memberReservation)
                .join(reservation.space, space)
                .where(memberReservation.member.in(members),
                        memberReservation.memberReservationStatus.eq(ReservationStatus.ACCEPTED),
                        space.dtype.eq("MeetingRoom"),
                        reservationCondition(startAt, endAt))
                .fetchFirst() != null;
    }


    @Override
    public boolean existsOverlappingRechargingRoomReservation(Member member, LocalDateTime startAt, LocalDateTime endAt) {
        return queryFactory.selectOne()
                .from(reservation)
                .join(reservation.memberReservations, memberReservation)
                .join(reservation.space, space)
                .where(memberReservation.member.eq(member),
                        memberReservation.memberReservationStatus.eq(ReservationStatus.ACCEPTED),
                        space.dtype.eq("RechargingRoom"),
                        reservationCondition(startAt, endAt))
                .fetchFirst() != null;
    }

    @Override
    public List<Reservation> findOverlappingRechargingRoomReservation(Member member, LocalDateTime startAt, LocalDateTime endAt) {
        return queryFactory.selectFrom(reservation)
                //리차징룸은 어차피 예약 다:1 회원 이라 fetchJoin 해도 무방
                .join(reservation.memberReservations, memberReservation).fetchJoin()
                .join(reservation.space, space)
                .where(memberReservation.member.eq(member),
                        memberReservation.memberReservationStatus.eq(ReservationStatus.ACCEPTED),
                        space.dtype.eq("RechargingRoom"),
                        reservationCondition(startAt, endAt))
                .fetch();
    }

    @Override
    public List<Reservation> findOverlappingRechargingRoomReservationInMembers(List<Member> members, LocalDateTime startAt, LocalDateTime endAt) {
        return queryFactory.selectFrom(reservation)
                //리차징룸은 어차피 예약 다:1 회원 이라 fetchJoin 해도 무방
                .join(reservation.memberReservations, memberReservation).fetchJoin()
                .join(reservation.space, space)
                .where(memberReservation.member.in(members),
                        memberReservation.memberReservationStatus.eq(ReservationStatus.ACCEPTED),
                        space.dtype.eq("RechargingRoom"),
                        reservationCondition(startAt, endAt))
                .fetch();
    }

    private BooleanBuilder reservationCondition(LocalDateTime startAt, LocalDateTime endAt) {
        return new BooleanBuilder()
                .and(reservation.reservationStartDateTime.between(startAt, endAt.minusSeconds(1))
                        .or(reservation.reservationEndDateTime.between(startAt.plusSeconds(1), endAt))
                        .or(reservation.reservationStartDateTime.before(startAt)
                                .and(reservation.reservationEndDateTime.after(endAt))));
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
        LocalDateTime endAt = now.plusDays(durationEnd).with(LocalTime.MAX).minusSeconds(1);

        return queryFactory.selectFrom(reservation)
                .join(reservation.memberReservations, memberReservation)
                .join(reservation.space, space).fetchJoin()
                .join(space.branch, branch).fetchJoin()
                .where(memberReservation.member.eq(member),
                        memberReservation.memberReservationStatus.eq(ReservationStatus.ACCEPTED),
                        reservation.reservationStartDateTime.between(startAt, endAt))
                .orderBy(reservation.reservationStartDateTime.asc())
                .fetch();
    }

    @Override
    public List<Reservation> findReservationsToday(Member member, LocalDateTime now) {

        return queryFactory.selectFrom(reservation)
                .join(reservation.memberReservations, memberReservation)
                .join(reservation.space, space).fetchJoin()
                .join(space.branch, branch).fetchJoin()
                .where(memberReservation.member.eq(member),
                        memberReservation.memberReservationStatus.eq(ReservationStatus.ACCEPTED),
                        (nowUsing(now)
                                .or(startAfterNowAndBeforeTodayMax(now))))
                .orderBy(reservation.reservationStartDateTime.asc())
                .fetch();
    }

    private BooleanExpression nowUsing(LocalDateTime now) {
        return reservation.reservationStartDateTime.before(now.plusSeconds(1))
                .and(reservation.reservationEndDateTime.after(now));
    }

    private BooleanExpression startAfterNowAndBeforeTodayMax(LocalDateTime now) {
        return reservation.reservationStartDateTime.after(now)
                .and(reservation.reservationStartDateTime.before(now.with(LocalTime.MAX).minusSeconds(1)));
    }

    @Override
    public List<Reservation> findAllByRechargingRoomListAndStartTimes(List<RechargingRoom> rechargingRooms, LocalDateTime startAt, LocalDateTime endAt) {
        return queryFactory.selectFrom(reservation)
                .join(reservation.space, space)
                .join(reservation.memberReservations, memberReservation)
                .where(space.in(rechargingRooms),
                        memberReservation.memberReservationStatus.eq(ReservationStatus.ACCEPTED),
                        space.dtype.eq("RechargingRoom"),
                        reservation.reservationStartDateTime.between(startAt, endAt))
                .fetch();
    }

    @Override
    public boolean existsOverlappingRechargingRoomReservationByStartAt(Member member, LocalDateTime startAt) {
        return queryFactory.selectOne()
                .from(reservation)
                .join(reservation.memberReservations, memberReservation)
                .join(reservation.space, space)
                .where(memberReservation.member.eq(member),
                        memberReservation.memberReservationStatus.eq(ReservationStatus.ACCEPTED),
                        space.dtype.eq("RechargingRoom"),
                        reservation.reservationStartDateTime.eq(startAt))
                .fetchFirst() != null;
    }

    @Override
    public boolean existsOverlappingMeetingRoomReservationsByStartAt(Member member, LocalDateTime startAt) {
        return queryFactory.selectOne()
                .from(reservation)
                .join(reservation.memberReservations, memberReservation)
                .join(reservation.space, space)
                .where(memberReservation.member.eq(member),
                        memberReservation.memberReservationStatus.eq(ReservationStatus.ACCEPTED),
                        space.dtype.eq("MeetingRoom"),
                        reservationCondition(startAt, startAt.plusMinutes(30)))
                .fetchFirst() != null;
    }
}
