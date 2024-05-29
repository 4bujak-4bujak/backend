package com.example.sabujak.branch.entity;

import com.example.sabujak.image.entity.Image;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;

@Getter @Setter
@Entity
@DiscriminatorValue("B")
public class BranchImage extends Image {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;
}
