package com.example.sabujak.space.repository;

import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.space.entity.RechargingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RechargingRoomRepository extends JpaRepository<RechargingRoom, Long> {

    List<RechargingRoom> findAllByBranch(Branch branch);
}
