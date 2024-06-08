package com.example.sabujak.space.repository;

import com.example.sabujak.space.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Long>,SpaceRepositoryCustom {
}
