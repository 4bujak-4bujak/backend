package com.example.sabujak.privatepost.repository;

import com.example.sabujak.privatepost.entity.PrivatePost;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.example.sabujak.privatepost.entity.QPrivatePost.privatePost;

@RequiredArgsConstructor
public class PrivatePostRepositoryImpl implements PrivatePostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<PrivatePost> findBySlice(Long lastPrivatePostId, Pageable pageable, String email) {
        List<PrivatePost> results = queryFactory.selectFrom(privatePost)
                .where(
                        ltPrivatePostId(lastPrivatePostId),
                        privatePost.member.memberEmail.eq(email)
                ).orderBy(privatePost.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return checkLastPage(pageable, results);
    }

    private BooleanExpression ltPrivatePostId(Long PrivatePostId) {
        if (PrivatePostId == null) return null;
        return privatePost.id.lt(PrivatePostId);
    }

    private Slice<PrivatePost> checkLastPage(Pageable pageable, List<PrivatePost> results){
        boolean hasNext = false;

        if (results.size() > pageable.getPageSize()) { // 뒤에 더 있음
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}
