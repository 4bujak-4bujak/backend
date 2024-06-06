package com.example.sabujak.reservation.dto.response;

import com.example.sabujak.member.entity.Member;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReservationResponseDto {

    public record CheckOverlap(Boolean alreadyUsing) {
    }


    @Getter
    public static class FindMemberList {
        private List<FindMember> memberCanInviteList = new ArrayList<>();
        private Set<FindMember> memberCantInviteList = new HashSet<>();

        public FindMemberList(List<FindMember> memberCanInviteList, Set<FindMember> memberCantInviteList) {
            this.memberCanInviteList = memberCanInviteList;
            this.memberCantInviteList = memberCantInviteList;
        }
    }

    public record FindMember(Long memberId,
                             String memberName,
                             String memberEmail,
                             String imageUrl) {
        public static FindMember from(Member member) {
            return new FindMember(
                    member.getMemberId(),
                    member.getMemberName(),
                    member.getMemberEmail(),
                    member.getImage().getImageUrl()
            );
        }
    }
}
