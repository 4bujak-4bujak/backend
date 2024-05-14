package com.example.sabujak.member.dto.request;

import com.example.sabujak.member.entity.Job;
import com.example.sabujak.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class MemberRequestDto {

    @Schema(description = "회원 가입 Request DTO")
    public record SignUp(
            @Schema(description = "이메일", example = "test@gmail.com")
            @NotNull
            @Email String email,

            @Schema(description = "비밀번호", example = "!password11")
            @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,16}$",
                    message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8자 이상 16자 이하로 입력해주세요.")
            @NotNull String password,

            @Schema(description = "이름", example = "주우민")
            @NotNull String memberName,

            @Schema(description = "직군", example = "REPRESENTATIVE")
            @NotNull Job memberJob,

            @Schema(description = "핸드폰 번호", example = "01012341234")
            @NotNull String memberPhone,

            @Schema(description = "sms 수신 동의 여부", example = "true")
            @NotNull boolean memberSmsAgree
    ) {
        public Member toEntity(String encryptedPassword) {
            return Member.builder()
                    .memberEmail(email)
                    .memberPassword(encryptedPassword)
                    .memberName(memberName)
                    .memberPhone(memberPhone)
                    .memberJob(memberJob)
                    .memberSmsAgree(memberSmsAgree)
                    .build();
        }
    }

    @Schema(description = "비밀번호 수정 Request DTO")
    public record ChangePassword(
            @Schema(description = "현재 비밀번호", example = "!password11")
            @NotNull String currentPassword,
            @Schema(description = "새 비밀번호", example = "!password12")
            @NotNull String newPassword) {
    }
}

