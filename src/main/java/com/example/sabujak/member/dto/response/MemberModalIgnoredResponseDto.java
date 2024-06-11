package com.example.sabujak.member.dto.response;

public record MemberModalIgnoredResponseDto(Long memberId, boolean ignored) {
    public static MemberModalIgnoredResponseDto of(Long memberId, boolean ignored) {
        return new MemberModalIgnoredResponseDto(memberId, ignored);
    }
}
