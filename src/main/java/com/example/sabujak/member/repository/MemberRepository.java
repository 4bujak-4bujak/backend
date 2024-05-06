package com.example.sabujak.member.repository;

import com.example.sabujak.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByMemberEmail(String memberEmail);
}
