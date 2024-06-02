package com.example.sabujak.space.repository;

import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.space.entity.FocusDesk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FocusDeskRepository extends JpaRepository<FocusDesk, Long> {

    List<FocusDesk> findAllByBranch(Branch branch);

    Integer countAllByBranch(Branch branch);
    Integer countAllByBranchAndAndCanReserve(Branch branch, boolean canReserve);
}
