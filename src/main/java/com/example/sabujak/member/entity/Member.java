package com.example.sabujak.member.entity;

import com.example.sabujak.common.entity.BaseEntity;
import com.example.sabujak.company.entity.Company;
import com.example.sabujak.privatepost.entity.PrivatePost;
import com.example.sabujak.reservation.entity.MemberReservation;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "member_modal_ignored", nullable = false)
    private boolean memberModalIgnored = false;

    @Column(name = "member_modal_ignored_time")
    private LocalDateTime memberModalIgnoredTime;

    @Column(name = "member_role")
    @Enumerated(EnumType.STRING)
    private Role memberRole = Role.ROLE_USER;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    @Setter
    private MemberImage image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @Setter
    private Company company;

    @OneToMany(mappedBy = "member")
    private List<MemberReservation> memberReservations = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<PrivatePost> privatePosts = new ArrayList<>();

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

    public void signOut() {
        this.memberDeleteCheck = true;
    }

    public void changeMemberPassword(String encryptedPassword) {
        this.memberPassword = encryptedPassword;
    }

    public void changeModalIgnoredAndTime(LocalDateTime now){
        this.memberModalIgnored = true;
        this.memberModalIgnoredTime = now;
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
