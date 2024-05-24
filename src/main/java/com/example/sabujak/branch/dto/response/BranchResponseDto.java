package com.example.sabujak.branch.dto.response;

import com.example.sabujak.branch.entity.Branch;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BranchResponseDto {
    private String branchName;
    private String branchAddress;

    private double branchLatitude;
    private double branchLongitude;

    public static BranchResponseDto fromEntity(Branch branch) {
        return new BranchResponseDto(
                branch.getBranchName(),
                branch.getBranchAddress(),
                branch.getBranchLatitude(),
                branch.getBranchLongitude());
    }

}
