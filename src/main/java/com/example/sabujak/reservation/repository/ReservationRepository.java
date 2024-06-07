package com.example.sabujak.reservation.repository;

import com.example.sabujak.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {

    @Query("select r from Reservation r join fetch r.space s join fetch s.branch where r.reservationId = :reservationId")
    Optional<Reservation> findByIdWithSpaceAndBranch(Long reservationId);
}
