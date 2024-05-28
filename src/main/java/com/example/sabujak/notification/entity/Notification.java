package com.example.sabujak.notification.entity;

import com.example.sabujak.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String targetUrl;
    private boolean isRead;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @Builder
    private Notification(String content, String targetUrl) {
        this.content = content;
        this.targetUrl = targetUrl;
        this.isRead = false;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setRead(Boolean isRead) {
        this.isRead = isRead;
    }
}

