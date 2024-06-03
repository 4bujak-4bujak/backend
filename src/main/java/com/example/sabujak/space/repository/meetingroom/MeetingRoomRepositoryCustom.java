package com.example.sabujak.space.repository.meetingroom;

import com.example.sabujak.space.entity.MeetingRoom;
import com.example.sabujak.space.entity.MeetingRoomType;
import com.example.sabujak.space.entity.Space;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingRoomRepositoryCustom {
    List<MeetingRoom> findMeetingRoomList(LocalDateTime startAt, LocalDateTime endAt, String branchName, List<MeetingRoomType> meetingRoomTypes, boolean projectorExists, boolean canVideoConference, boolean isPrivate, String sortTarget, String sortDirection);

    Integer countTotalMeetingRoom(String branchName);
    Integer countActiveMeetingRoom(LocalDateTime now, String branchName);
}
