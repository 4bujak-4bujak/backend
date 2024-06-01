package com.example.sabujak.space.dto.response;

import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.space.entity.MeetingRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class SpaceResponseDto {


    public record MeetingRoomDto(Long meetingRoomId,
                                 String meetingRoomName,
                                 int meetingRoomFloor,
                                 int meetingRoomCapacity,
                                 String meetingRoomImage) {
        public static MeetingRoomDto from(MeetingRoom meetingRoom) {
            return new MeetingRoomDto(
                    meetingRoom.getSpaceId(),
                    meetingRoom.getSpaceName(),
                    meetingRoom.getSpaceFloor(),
                    meetingRoom.getMeetingRoomCapacity(),
                    null
            );
        }
    }

    @AllArgsConstructor
    @Getter
    public static class MeetingRoomDetails {
        private Long branchId;
        private String branchName;
        private Long meetingRoomId;
        private String meetingRoomName;
        private int meetingRoomFloor;
        private int meetingRoomCapacity;
        private String meetingRoomImage;
        private Set<String> equipments = new HashSet<>();


        public static MeetingRoomDetails of(Branch branch, MeetingRoom meetingRoom) {
            Set<String> equipments = new HashSet<>();
            if (meetingRoom.isPrivate()) {
                equipments.add("프라이빗");
            }
            if (meetingRoom.isProjectorExists()) {
                equipments.add("빔프로젝터");
            }
            if (meetingRoom.isCanVideoConference()) {
                equipments.add("화상장비");
            }

            return new MeetingRoomDetails(
                    branch.getBranchId(),
                    branch.getBranchName(),
                    meetingRoom.getSpaceId(),
                    meetingRoom.getSpaceName(),
                    meetingRoom.getSpaceFloor(),
                    meetingRoom.getMeetingRoomCapacity(),
                    null,
                    equipments
            );
        }

    }
}
