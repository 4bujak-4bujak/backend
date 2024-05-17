package com.example.sabujak.common.image;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("S")
public class SpaceImage extends Image{
}
