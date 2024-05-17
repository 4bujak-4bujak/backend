package com.example.sabujak.common.image;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("P")
public class PostImage extends Image{
}
