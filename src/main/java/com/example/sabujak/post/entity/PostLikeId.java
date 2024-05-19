package com.example.sabujak.post.entity;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
public class PostLikeId implements Serializable {

    private Long postId;
    private String memberEmail;

    @Builder
    private PostLikeId(Long postId, String memberEmail) {
        this.postId = postId;
        this.memberEmail = memberEmail;
    }
}
