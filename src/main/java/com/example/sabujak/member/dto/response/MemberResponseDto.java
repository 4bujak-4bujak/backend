package com.example.sabujak.member.dto.response;

import com.example.sabujak.member.entity.Job;
import com.example.sabujak.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

public class MemberResponseDto {

    @Schema(description = "회원 정보 Response DTO")
    public record AllInformation(@Schema(description = "이메일", example = "test@gmail.com")
                                 String memberEmail,
                                 @Schema(description = "이름", example = "주우민")
                                 String memberName,
                                 @Schema(description = "회사", example = "네이버")
                                 String companyName,
                                 @Schema(description = "닉네임", example = "zoomin")
                                 String memberNickName,
                                 @Schema(description = "직업군", example = "OWNER")
                                 Job memberJob,
                                 @Schema(description = "sms동의여부", example = "true")
                                 boolean memberSmsAgree,
                                 @Schema(description = "핸드폰 번호", example = "01012341234")
                                 String memberPhone,
                                 @Schema(description = "이미지 url", example = "image.com")
                                 String imageUrl) {
        public static MemberResponseDto.AllInformation of(Member member) {
            return new MemberResponseDto.AllInformation(
                    member.getMemberEmail(),
                    member.getMemberName(),
                    member.getCompany().getCompanyName(),
                    member.getMemberNickname(),
                    member.getMemberJob(),
                    member.isMemberSmsAgree(),
                    member.getMemberPhone(),
                    member.getImage().getImageUrl());
        }
    }

    @Schema(description = "회원 실명 + 회사 Response DTO")
    public record NameAndCompany(@Schema(description = "이름", example = "주우민")
                                 String memberName,
                                 @Schema(description = "회사", example = "네이버")
                                 String companyName) {
        public static MemberResponseDto.NameAndCompany of(Member member) {
            return new MemberResponseDto.NameAndCompany(
                    member.getMemberName(),
                    member.getCompany().getCompanyName()
            );
        }
    }
}
