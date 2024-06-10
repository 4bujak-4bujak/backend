package com.example.sabujak.space.repository;

import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.branch.entity.QBranch;
import com.example.sabujak.reservation.entity.ReservationStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.sabujak.branch.entity.QBranch.branch;
import static com.example.sabujak.reservation.entity.QMemberReservation.memberReservation;
import static com.example.sabujak.reservation.entity.QReservation.reservation;
import static com.example.sabujak.space.entity.QSpace.space;

@RequiredArgsConstructor
public class SpaceRepositoryImpl implements SpaceRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Integer countAllRechargingRoomByBranch(Long branchId) {
        return Math.toIntExact(queryFactory.select(space.count())
                .from(space)
                .join(space.branch, branch)
                .where(
                        space.dtype.eq("RechargingRoom"),
                        space.branch.branchId.eq(branchId)
                )
                .fetchFirst());
    }

    @Override
    public List<Long> countAllSpaceByBranch(Branch branch) {
        return queryFactory.select(space.count())
                .from(space)
                .join(space.branch, QBranch.branch)
                .where(
                        space.branch.eq(branch)
                )
                .groupBy(space.dtype)
                .orderBy(space.dtype.asc())
                .fetch();
    }

    @Override
    public Long countUsingSpaceByBranchAndDtype(Branch branch, LocalDateTime now, String dtype) {
        return queryFactory.select(space.count())
                .from(space)
                .join(space.branch, QBranch.branch)
                .join(space.reservations, reservation)
                .join(reservation.memberReservations, memberReservation)
                .where(
                        space.branch.eq(branch),
                        nowUsing(now),
                        memberReservation.memberReservationStatus.eq(ReservationStatus.ACCEPTED),
                        space.dtype.eq(dtype)
                )
                .fetchFirst();
    }

    private BooleanExpression nowUsing(LocalDateTime now) {
        return reservation.reservationStartDateTime.before(now.plusSeconds(1))
                .and(reservation.reservationEndDateTime.after(now));
    }
}
