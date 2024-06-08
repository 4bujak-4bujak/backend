package com.example.sabujak.space.repository;

import com.example.sabujak.branch.entity.QBranch;
import com.example.sabujak.space.entity.MeetingRoomType;
import com.example.sabujak.space.entity.QSpace;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.example.sabujak.branch.entity.QBranch.branch;
import static com.example.sabujak.space.entity.QSpace.space;

@RequiredArgsConstructor
public class SpaceRepositoryImpl implements SpaceRepositoryCustom{
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
}
