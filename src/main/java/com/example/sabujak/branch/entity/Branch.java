package com.example.sabujak.branch.entity;

import com.example.sabujak.space.entity.Space;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "branch")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private Long branchId;

    @NotNull
    @Column(name = "branch_name")
    private String branchName;

    @NotNull
    @Column(name = "branch_address")
    private String branchAddress;

    @NotNull
    @Column(name = "branch_latitude")
    private double branchLatitude;

    @NotNull
    @Column(name = "branch_longitude")
    private double branchLongitude;

    @OneToMany(mappedBy = "branch")
    private List<Space> spaceList = new ArrayList<>();

    @Builder
    private Branch(String branchName, String branchAddress, Double branchLatitude, Double branchLongitude) {
        this.branchName = branchName;
        this.branchAddress = branchAddress;
        this.branchLatitude = branchLatitude;
        this.branchLongitude = branchLongitude;
    }
}

