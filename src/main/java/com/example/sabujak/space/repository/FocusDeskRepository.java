package com.example.sabujak.space.repository;

import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.space.entity.FocusDesk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FocusDeskRepository extends JpaRepository<FocusDesk, Long> {

    Integer countAllByBranch(Branch branch);
    Integer countAllByBranchAndAndCanReserve(Branch branch, boolean canReserve);
}
