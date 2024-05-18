package com.example.sabujak.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike {

    @EmbeddedId
    @Column(name = "post_like_id")
    private PostLikeId id;

    @Builder
    private PostLike(PostLikeId id) {
        this.id = id;
    }
}
