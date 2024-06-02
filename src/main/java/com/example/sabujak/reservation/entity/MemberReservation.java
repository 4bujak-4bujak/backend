package com.example.sabujak.reservation.entity;

import com.example.sabujak.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_reservation")
public class MemberReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_reservation_id")
    private Long memberReservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_reservation_status")
    private ReservationStatus memberReservationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_reservation_type")
    private MemberReservationType memberReservationType;

    public static MemberReservation createMemberReservation(Reservation reservation, Member member, MemberReservationType memberReservationType) {
        MemberReservation memberReservation = new MemberReservation();

        memberReservation.reservation = reservation;
        memberReservation.member = member;

        memberReservation.memberReservationStatus = ReservationStatus.ACCEPTED;
        memberReservation.memberReservationType = memberReservationType;

        reservation.getMemberReservations().add(memberReservation);
        member.getMemberReservations().add(memberReservation);
        return memberReservation;
    }
}
