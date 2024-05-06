package com.example.sabujak.member.entity;

import com.example.sabujak.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseEntity implements UserDetails {
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

    @NotNull
    @Column(name = "member_birth")
    private LocalDate memberBirthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_gender")
    private Gender memberGender;

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
    public Member(String memberEmail, String memberPassword, String memberName, String memberPhone, LocalDate memberBirthDate, Job memberJob, Gender memberGender, boolean memberSmsAgree) {
        this.memberEmail = memberEmail;
        this.memberPassword = memberPassword;
        this.memberName = memberName;
        this.memberPhone = memberPhone;
        this.memberBirthDate = memberBirthDate;
        this.memberGender = memberGender;
        this.memberJob = memberJob;
        this.memberSmsAgree = memberSmsAgree;
        this.memberNickname = generateRandomNickname();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.memberRole.name()));
    }

    @Override
    public String getPassword() {
        return memberPassword;
    }

    @Override
    public String getUsername() {
        return memberEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void delete() {
        this.memberDeleteCheck = true;
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
