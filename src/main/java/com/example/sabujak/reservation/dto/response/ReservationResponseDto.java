package com.example.sabujak.reservation.dto.response;

public class ReservationResponseDto {

    public record CheckOverlap(Boolean alreadyUsing) {
    }

//    public record FindMember(Long memberId,
//                             String memberName,
//                             String memberEmail,
//                             String imageUrl) {
//        public static FindMember from(Member member) {
//            return new FindMember(
//                    member.getMemberId(),
//                    member.getMemberName(),
//                    member.getMemberEmail(),
//                    member.getImage().getImageUrl()
//            );
//        }
//    }
}
