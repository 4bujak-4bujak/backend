package com.example.sabujak.space.entity;

import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.reservation.entity.Reservation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "space")
@DiscriminatorColumn
public abstract class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "space_id")
    private Long spaceId;

    @NotNull
    @Column(name = "space_name")
    private String spaceName;

    @NotNull
    @Column(name = "space_floor")
    private int spaceFloor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @OneToMany(mappedBy = "space")
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "space")
    @OrderBy("imageId asc")
    private List<SpaceImage> imageList = new ArrayList<>();


}
