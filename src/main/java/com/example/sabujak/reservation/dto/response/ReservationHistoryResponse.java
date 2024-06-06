package com.example.sabujak.reservation.dto.response;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.reservation.entity.MemberReservationType;
import com.example.sabujak.reservation.entity.Reservation;
import com.example.sabujak.space.dto.SpaceType;
import com.example.sabujak.space.entity.FocusDesk;
import com.example.sabujak.space.entity.Space;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationHistoryResponse {

    public record TodayReservationCount(Integer count) {
    }

    @Getter
    public static class ReservationForList {
        private Long reservationId;
        private String reservationName;
        private String branchName;
        private String spaceName;
        private String startAt;
        private String endAt;
        private SpaceType spaceType;
        private List<String> memberImageUrls;
        private MemberReservationType memberType;

        public static ReservationForList of(Reservation reservation, Space space, List<Member> members, MemberReservationType memberType) {
            ReservationForList reservationForList = new ReservationForList();
            reservationForList.reservationId = reservation.getReservationId();
            reservationForList.reservationName = reservation.getReservationName();
            reservationForList.branchName = space.getBranch().getBranchName();

            reservationForList.spaceName = space.getSpaceName();
            if(space instanceof FocusDesk){
                reservationForList.spaceName += " " + ((FocusDesk) space).getFocusDeskNumber();
            }

            reservationForList.startAt = reservation.getReservationStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            reservationForList.endAt = reservation.getReservationEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            String spaceType = space.getDtype().toUpperCase();
            reservationForList.spaceType = SpaceType.valueOf(spaceType);

            reservationForList.memberImageUrls = members.stream()
                    .map(member -> member.getImage().getImageUrl())
                    .collect(Collectors.toList());

            reservationForList.memberType = memberType;

            return reservationForList;
        }
    }
}
