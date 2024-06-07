package com.example.sabujak.reservation.repository;

import com.example.sabujak.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {

    @Query(
            value = "select r " +
                    "from Reservation r " +
                    "join fetch r.memberReservations mr " +
                    "join fetch mr.member m " +
                    "where r.reservationId = :reservationId"
    )
    Optional<Reservation> findWithMemberReservationsAndMembersById(
            @Param("reservationId") Long reservationId
    );
}
