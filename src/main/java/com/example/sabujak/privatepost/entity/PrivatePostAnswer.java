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
public class PrivatePostAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "private_post_answer_id")
    private Long id;

    @Column(nullable = false, name = "private_post_answer_content")
    private String content;

    @Setter
    @OneToOne(mappedBy = "privatePostAnswer", fetch = LAZY)
    private PrivatePost privatePost;


    @Builder
    private PrivatePostAnswer(String content) {
        this.content = content;
    }


}
