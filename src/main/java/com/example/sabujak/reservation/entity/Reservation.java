package com.example.sabujak.reservation.entity;

import com.example.sabujak.common.entity.BaseEntity;
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

    @OneToMany(mappedBy = "reservation")
    private List<MemberReservation> memberReservations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;
}
