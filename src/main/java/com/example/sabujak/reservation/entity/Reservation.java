package com.example.sabujak.reservation.entity;

import com.example.sabujak.common.entity.BaseEntity;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.space.entity.Space;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservation")
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(name = "reservation_start_date_time")
    private LocalDateTime reservationStartDateTime;

    @Column(name = "reservation_end_date_time")
    private LocalDateTime reservationEndDateTime;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<MemberReservation> memberReservations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    public static Reservation createReservation(LocalDateTime reservationStartDateTime, LocalDateTime reservationEndDateTime, Space space) {
        Reservation reservation = new Reservation();
        reservation.reservationStartDateTime = reservationStartDateTime;
        reservation.reservationEndDateTime = reservationEndDateTime;
        reservation.space = space;

        return reservation;
    }

    public void addMemberReservation(Member member, MemberReservationType memberReservationType) {
        MemberReservation.createMemberReservation(this, member, memberReservationType);
    }

    public void endUse(LocalDateTime endDateTime) {
        this.reservationEndDateTime = endDateTime;
    }
}
