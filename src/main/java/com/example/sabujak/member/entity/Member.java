package com.example.sabujak.member.entity;

import com.example.sabujak.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @NotNull
    @Email
    @Column(name = "member_email")
    private String memberEmail;

    @NotNull
    @Column(name = "member_pw")
    private String memberPassword;

    @NotNull
    @Column(name = "member_name")
    private String memberName;

    @NotNull
    @Column(name = "member_nickname")
    private String memberNickname;

    @NotNull
    @Column(name = "member_phone")
    private String memberPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_job")
    private Job memberJob;

    @Column(name = "member_smsAgree", nullable = false)
    private boolean memberSmsAgree;

    @Column(name = "member_deleteCheck", nullable = false)
    private Boolean memberDeleteCheck = false;

    @Column(name = "member_role")
    @Enumerated(EnumType.STRING)
    private Role memberRole = Role.ROLE_USER;

    @Builder
    private Member(String memberEmail, String memberPassword, String memberName, String memberPhone, Job memberJob, boolean memberSmsAgree) {
        this.memberEmail = memberEmail;
        this.memberPassword = memberPassword;
        this.memberName = memberName;
        this.memberPhone = memberPhone;
        this.memberJob = memberJob;
        this.memberSmsAgree = memberSmsAgree;
        this.memberNickname = generateRandomNickname();
    }

    public void delete() {
        this.memberDeleteCheck = true;
    }

    public void changeMemberPassword(String encryptedPassword) {
        this.memberPassword = encryptedPassword;
    }

    private String generateRandomNickname() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }

        return result.toString();
    }
}
