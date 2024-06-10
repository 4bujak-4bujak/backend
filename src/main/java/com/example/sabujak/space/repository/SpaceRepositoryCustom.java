package com.example.sabujak.space.repository;

import com.example.sabujak.branch.entity.Branch;

import java.time.LocalDateTime;
import java.util.List;

public interface SpaceRepositoryCustom {
    Integer countAllRechargingRoomByBranch(Long branchId);

    List<Long> countAllSpaceByBranch(Branch branch);
    Long countUsingSpaceByBranchAndDtype(Branch branch, LocalDateTime now, String dtype);
}
