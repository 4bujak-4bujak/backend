package com.example.sabujak.post.entity;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@NoArgsConstructor(access = PROTECTED)
@ToString
public class PostLikeId implements Serializable {

    private Long postId;
    private String memberEmail;

    @Builder
    private PostLikeId(Long postId, String memberEmail) {
        this.postId = postId;
        this.memberEmail = memberEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostLikeId that = (PostLikeId) o;
        return Objects.equals(postId, that.postId) && Objects.equals(memberEmail, that.memberEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, memberEmail);
    }
}
