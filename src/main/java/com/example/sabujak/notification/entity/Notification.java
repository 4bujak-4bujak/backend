package com.example.sabujak.notification.entity;

import com.example.sabujak.common.entity.BaseEntity;
import com.example.sabujak.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
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

    private String image;
    private String title;
    private String content;
    private Long targetId;

    @Enumerated(STRING)
    private NotificationType type;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @Builder
    private Notification(String image, String title, String content, Long targetId, NotificationType type) {
        this.image = image;
        this.title = title;
        this.content = content;
        this.targetId = targetId;
        this.type = type;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}

