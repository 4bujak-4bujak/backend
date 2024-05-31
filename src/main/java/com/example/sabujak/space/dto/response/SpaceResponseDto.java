package com.example.sabujak.space.dto.response;

import com.example.sabujak.space.entity.MeetingRoom;

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
}
