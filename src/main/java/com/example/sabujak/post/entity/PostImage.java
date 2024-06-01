package com.example.sabujak.post.entity;

import com.example.sabujak.image.entity.Image;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("P")
public class PostImage extends Image {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    private PostImage(String path) {
        super(path);
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
