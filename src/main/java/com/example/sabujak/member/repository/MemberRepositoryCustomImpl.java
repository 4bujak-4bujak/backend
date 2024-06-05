package com.example.sabujak.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

//    @Override
//    public List<Member> findMemberCanInvite(Company company, Long myId, String searchTerm) {
//
//        return queryFactory.selectFrom(member)
//                .join(member.company, QCompany.company)
//                .leftJoin(member.image, memberImage).fetchJoin()
//                .leftJoin(member.memberReservations, memberReservation)
//                .where(member.company.eq(company), member.memberId.ne(myId),
//                        searchKeyword(searchTerm))
//                .fetch();
//    }
//
//    private BooleanExpression searchKeyword(String searchTerm) {
//        if (searchTerm.isBlank()) {
//            return null;
//        }
//        return member.memberName.contains(searchTerm)
//                .or(member.memberEmail.contains(searchTerm));
//    }
}
