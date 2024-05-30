package com.example.sabujak.branch.entity;

import com.example.sabujak.space.entity.Space;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name = "branch_phone_number")
    private String branchPhoneNumber;

    @Column(name = "road_from_station")
    private String roadFromStation; //찾아 오는길

    @ElementCollection
    @CollectionTable(name = "station_names", joinColumns = @JoinColumn(name = "branch_id"))
    @Column(name = "station_to_branch")
    private Set<String> stationToBranch = new HashSet<>(); // 지하철 호선


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

