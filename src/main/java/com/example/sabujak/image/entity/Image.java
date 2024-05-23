package com.example.sabujak.image.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.InheritanceType.JOINED;
import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@NoArgsConstructor(access = PROTECTED)
@Inheritance(strategy = JOINED)
@Entity
@Table(name = "image")
@DiscriminatorColumn
public abstract class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @NotNull
    @Column(name = "image_url")
    private String imageUrl;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Image(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
