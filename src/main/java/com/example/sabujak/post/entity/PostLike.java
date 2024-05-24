package com.example.sabujak.post.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class PostLike {

    @EmbeddedId
    private PostLikeId id;

    @Builder
    private PostLike(PostLikeId id) {
        this.id = id;
    }
}
