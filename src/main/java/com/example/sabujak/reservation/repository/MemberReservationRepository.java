package com.example.sabujak.reservation.repository;

import com.example.sabujak.reservation.entity.MemberReservation;
import com.example.sabujak.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberReservationRepository extends JpaRepository<MemberReservation, Long> {
    @Query("select mr from MemberReservation mr join fetch mr.member m join fetch m.image where mr.reservation in :reservations and mr.memberReservationStatus = 'ACCEPTED'")
    List<MemberReservation> findMemberReservationsByReservations(List<Reservation> reservations);

    @Query("select mr from MemberReservation mr join fetch mr.member m join fetch m.image where mr.reservation = :reservation")
    List<MemberReservation> findByReservation(Reservation reservation);
}
