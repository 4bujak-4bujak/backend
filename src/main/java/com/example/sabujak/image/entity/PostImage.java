package com.example.sabujak.image.entity;

import com.example.sabujak.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@Entity
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("P")
public class PostImage extends Image{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    @Builder
    private PostImage(String imageUrl) {
        super(imageUrl);
    }

}
