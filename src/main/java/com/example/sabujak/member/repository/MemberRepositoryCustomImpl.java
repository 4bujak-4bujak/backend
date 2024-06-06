package com.example.sabujak.member.repository;

import com.example.sabujak.company.entity.Company;
import com.example.sabujak.company.entity.QCompany;
import com.example.sabujak.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.sabujak.member.entity.QMember.member;
import static com.example.sabujak.member.entity.QMemberImage.memberImage;
import static com.example.sabujak.reservation.entity.QMemberReservation.memberReservation;
import static com.example.sabujak.reservation.entity.QReservation.reservation;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> searchMembers(Company company, Long myId, String searchTerm) {

        return queryFactory.selectFrom(member)
                .join(member.company, QCompany.company)
                .leftJoin(member.image, memberImage).fetchJoin()
                .where(member.company.eq(company), member.memberId.ne(myId),
                        searchKeyword(searchTerm))
                .fetch();
    }

    private BooleanExpression searchKeyword(String searchTerm) {
        if (searchTerm == null) {
            return null;
        } else if (searchTerm.isBlank()) {
            return null;
        }
        return member.memberName.contains(searchTerm)
                .or(member.memberEmail.contains(searchTerm));
    }

    @Override
    public List<Member> searchMembersCanInviteInMembers(List<Member> members, LocalDateTime startAt, LocalDateTime endAt) {
        return queryFactory.selectFrom(member)
                .join(member.memberReservations, memberReservation)
                .where(member.in(members), reservationCondition(startAt, endAt))
                .fetch();
    }

    private BooleanExpression reservationCondition(LocalDateTime startAt, LocalDateTime endAt) {
        return JPAExpressions.selectOne()
                .from(memberReservation)
                .join(memberReservation.reservation, reservation)
                .where(memberReservation.member.eq(member),
                        reservation.reservationStartDateTime.between(startAt, endAt.minusNanos(1))
                                .or(reservation.reservationEndDateTime.between(startAt.plusNanos(1), endAt))
                                .or(reservation.reservationStartDateTime.before(startAt)
                                        .and(reservation.reservationEndDateTime.after(endAt))))
                .notExists();
    }
}
