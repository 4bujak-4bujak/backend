package com.example.sabujak.notification.entity;

import com.example.sabujak.common.entity.BaseEntity;
import com.example.sabujak.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String content;
    private String targetUrl;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @Builder
    private Notification(String content, String targetUrl) {
        this.content = content;
        this.targetUrl = targetUrl;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}

