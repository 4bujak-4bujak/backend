package com.example.sabujak.space.repository.meetingroom;

import com.example.sabujak.reservation.entity.ReservationStatus;
import com.example.sabujak.space.entity.MeetingRoom;
import com.example.sabujak.space.entity.MeetingRoomType;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.sabujak.branch.entity.QBranch.branch;
import static com.example.sabujak.reservation.entity.QMemberReservation.memberReservation;
import static com.example.sabujak.reservation.entity.QReservation.reservation;
import static com.example.sabujak.space.entity.QMeetingRoom.meetingRoom;

@RequiredArgsConstructor
public class MeetingRoomRepositoryImpl implements MeetingRoomRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    @Override
    public List<MeetingRoom> findMeetingRoomList(LocalDateTime startAt, LocalDateTime endAt, String branchName, List<MeetingRoomType> meetingRoomTypes, boolean projectorExists, boolean canVideoConference, boolean isPrivate, String sortTarget, String sortDirection) {

        return queryFactory.selectFrom(meetingRoom)
                .join(meetingRoom.branch, branch)
                .where(meetingRoom.branch.branchName.eq(branchName),
                        meetingRoom.meetingRoomType.in(meetingRoomTypes),
                        projectorCondition(projectorExists),
                        videoConferenceCondition(canVideoConference),
                        privateCondition(isPrivate),
                        reservationCondition(startAt, endAt))
                .orderBy(meetingRoomPrimarySort(sortTarget, sortDirection), meetingRoomSecondarySort(sortTarget))
                .fetch();
    }

    @Override
    public Integer countTotalMeetingRoom(String branchName) {
        return Math.toIntExact(queryFactory.select(meetingRoom.count())
                .from(meetingRoom)
                .where(meetingRoom.branch.branchName.eq(branchName))
                .fetchFirst());

    }

    @Override
    public Integer countActiveMeetingRoom(LocalDateTime now, String branchName) {
        return Math.toIntExact(queryFactory.select(meetingRoom.count())
                .from(meetingRoom)
                .where(meetingRoom.branch.branchName.eq(branchName),
                        reservationConditionV2(now))
                .fetchFirst());
    }

    private BooleanExpression reservationCondition(LocalDateTime startAt, LocalDateTime endAt) {
        return JPAExpressions.selectOne()
                .from(reservation)
                .join(reservation.memberReservations, memberReservation)
                .where(reservation.space.spaceId.eq(meetingRoom.spaceId),
                        memberReservation.memberReservationStatus.eq(ReservationStatus.ACCEPTED),
                        reservation.reservationStartDateTime.between(startAt, endAt.minusSeconds(1))
                                .or(reservation.reservationEndDateTime.between(startAt.plusSeconds(1), endAt))
                                .or(reservation.reservationStartDateTime.before(startAt)
                                        .and(reservation.reservationEndDateTime.after(endAt))))
                .notExists();
    }

    private BooleanExpression reservationConditionV2(LocalDateTime now) { // 현재 기준 사용중인 회의실 개수 반환
        return JPAExpressions.selectOne()
                .from(reservation)
                .join(reservation.memberReservations, memberReservation)
                .where(reservation.space.spaceId.eq(meetingRoom.spaceId),
                        memberReservation.memberReservationStatus.eq(ReservationStatus.ACCEPTED),
                        reservation.reservationStartDateTime.before(now),
                        reservation.reservationEndDateTime.after(now))
                .exists();
    }


    private BooleanExpression projectorCondition(boolean projectorExists) {
        if (projectorExists) {
            return meetingRoom.projectorExists.isTrue();
        }
        return null;
    }

    private BooleanExpression videoConferenceCondition(boolean canVideoConference) {
        if (canVideoConference) {
            return meetingRoom.canVideoConference.isTrue();
        }
        return null;
    }

    private BooleanExpression privateCondition(boolean isPrivate) {
        if (isPrivate) {
            return meetingRoom.isPrivate.isTrue();
        }
        return null;
    }

    private OrderSpecifier<?> meetingRoomPrimarySort(String sortTarget, String sortDirection) {
        Order direction = sortDirection.equals("ASC") ? Order.ASC : Order.DESC;

        switch (sortTarget) {
            case "roomCapacity":
                return new OrderSpecifier<>(direction, meetingRoom.meetingRoomCapacity);
            case "roomFloor":
                return new OrderSpecifier<>(direction, meetingRoom.spaceFloor);
        }
        return new OrderSpecifier<>(direction, meetingRoom.meetingRoomCapacity);
    }

    private OrderSpecifier<?> meetingRoomSecondarySort(String sortTarget) {

        if (sortTarget.equals("roomCapacity")) {
            return new OrderSpecifier<>(Order.ASC, meetingRoom.spaceFloor);
        } else {
            return new OrderSpecifier<>(Order.ASC, meetingRoom.meetingRoomCapacity);
        }
    }
}
