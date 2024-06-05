package com.example.sabujak.privatepost.entity;

import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.common.entity.BaseEntity;
import com.example.sabujak.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class PrivatePost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20) //최대 20자
    private String title;

    @Column(nullable = false)
    private String content;

    @Setter
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Setter
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "private_post_answer_id")
    private PrivatePostAnswer privatePostAnswer;


    @Builder
    private PrivatePost(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
