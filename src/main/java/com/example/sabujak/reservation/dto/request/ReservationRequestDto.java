package com.example.sabujak.reservation.dto.request;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.reservation.entity.MemberReservationType;
import com.example.sabujak.reservation.entity.Reservation;
import com.example.sabujak.space.entity.FocusDesk;
import com.example.sabujak.space.entity.MeetingRoom;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ReservationRequestDto {

    public record MeetingRoomDto(
            @NotNull
            String reservationName,
            @Positive
            Long meetingRoomId,
            @Future
            LocalDateTime startAt,
            @Future
            LocalDateTime endAt,
            List<Long> memberIds) {

        public Reservation toReservationEntity(MeetingRoom meetingRoom, Member representative, List<Member> participants) {
            Reservation reservation = Reservation.createReservation(reservationName, startAt, endAt, meetingRoom);
            reservation.addMemberReservation(representative, MemberReservationType.REPRESENTATIVE);
            participants.forEach(member -> reservation.addMemberReservation(member, MemberReservationType.PARTICIPANT));

            return reservation;
        }
    }

    public record FocusDeskDto(
            @Positive
            Long focusDeskId) {

        public Reservation toReservationEntity(FocusDesk focusDesk, LocalDateTime startAt, Member member) {
            LocalDateTime endAt = startAt.plusDays(1).with(LocalTime.MIDNIGHT);

            Reservation reservation = Reservation.createReservation("포커스존", startAt, endAt, focusDesk);
            reservation.addMemberReservation(member, MemberReservationType.REPRESENTATIVE);

            return reservation;
        }
    }
}
