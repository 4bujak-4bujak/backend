package com.example.sabujak.space.repository.meetingroom;

import com.example.sabujak.space.entity.MeetingRoom;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.sabujak.space.entity.QMeetingRoom.meetingRoom;

@RequiredArgsConstructor
public class MeetingRoomRepositoryImpl implements MeetingRoomRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    @Override
    public List<MeetingRoom> findMeetingRoomList(String branchName, int roomCapacity, boolean projectorExists, boolean canVideoConference, boolean isPrivate, String sortTarget, String sortDirection) {

        return queryFactory.selectFrom(meetingRoom)
                .where(meetingRoom.branch.branchName.eq(branchName),
                        meetingRoom.meetingRoomCapacity.goe(roomCapacity),
                        projectorCondition(projectorExists),
                        videoConferenceCondition(canVideoConference),
                        privateCondition(isPrivate))
                .orderBy(meetingRoomPrimarySort(sortTarget, sortDirection), meetingRoomSecondarySort(sortTarget))
                .fetch();
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
