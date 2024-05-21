package com.example.sabujak.space.entity;

import com.example.sabujak.branch.entity.Branch;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "space")
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "space_id")
    private Long spaceId;

    @NotNull
    @Column(name = "space_name")
    private String spaceName;

    @NotNull
    @Column(name = "space_capacity")
    private int spaceCapacity;

    @NotNull
    @Column(name = "space_floor")
    private int spaceFloor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;



}
