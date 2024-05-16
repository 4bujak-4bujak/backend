package com.example.sabujak.member.dto.response;

import com.example.sabujak.member.entity.Job;
import com.example.sabujak.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "회원 정보 Response DTO")
@Getter
@AllArgsConstructor
public class MemberResponseDto {

    @Schema(description = "이메일", example = "test@gmail.com")
    private String memberEmail;
    @Schema(description = "이름", example = "주우민")
    private String memberName;
    @Schema(description = "닉네임", example = "zoomin")
    private String memberNickName;
    @Schema(description = "직업군", example = "OWNER")
    private Job memberJob;
    @Schema(description = "sms동의여부", example = "true")
    private boolean memberSmsAgree;

    public static MemberResponseDto of(Member member) {
        return new MemberResponseDto(
                member.getMemberEmail(),
                member.getMemberName(),
                member.getMemberNickname(),
                member.getMemberJob(),
                member.isMemberSmsAgree());
    }
}
