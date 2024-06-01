package com.example.sabujak.space.entity;

import com.example.sabujak.image.entity.Image;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;

@Getter @Setter
@Entity
@DiscriminatorValue("S")
public class SpaceImage extends Image {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "space_id")
    private Space space;
}
