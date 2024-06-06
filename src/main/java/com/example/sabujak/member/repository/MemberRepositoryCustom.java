package com.example.sabujak.member.repository;

import com.example.sabujak.company.entity.Company;
import com.example.sabujak.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> searchMembers(Company company, Long myId, String searchTerm);
    List<Member> searchMembersCanInviteInMembers(List<Member> members, LocalDateTime startAt, LocalDateTime endAt);
}
