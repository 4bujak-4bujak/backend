package com.example.sabujak.branch.dto.response;

import com.example.sabujak.branch.entity.Branch;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class BranchResponseDto {
    private String branchName;
    private String branchAddress;

    private double branchLatitude;
    private double branchLongitude;

    private String branchPhoneNumber;
    private String roadFromStation;
    private Set<String> stationToBranch;

    public static BranchResponseDto fromEntity(Branch branch) {
        return new BranchResponseDto(
                branch.getBranchName(),
                branch.getBranchAddress(),
                branch.getBranchLatitude(),
                branch.getBranchLongitude(),
                branch.getBranchPhoneNumber(),
                branch.getRoadFromStation(),
                branch.getStationToBranch()
        );
    }

}
