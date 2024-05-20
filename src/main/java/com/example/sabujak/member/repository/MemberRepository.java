package com.example.sabujak.member.repository;

import com.example.sabujak.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberEmail(String memberEmail);
    Boolean existsByMemberEmail(String memberEmail);
    Boolean existsByMemberPhone(String memberPhone);

    @EntityGraph(attributePaths = {"company"})
    Optional<Member> findWithCompanyByMemberEmail(String memberEmail);
}
