package com.example.sabujak.member.repository;

import com.example.sabujak.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByMemberEmail(String memberEmail);

    Boolean existsByMemberEmail(String memberEmail);

    Boolean existsByMemberPhone(String memberPhone);

    @Query("SELECT m FROM Member m JOIN FETCH m.company JOIN FETCH m.image WHERE m.memberEmail = :memberEmail")
    Optional<Member> findWithCompanyAndImageByMemberEmail(String memberEmail);

    List<Member> findByMemberIdIn(List<Long> memberIds);
}
