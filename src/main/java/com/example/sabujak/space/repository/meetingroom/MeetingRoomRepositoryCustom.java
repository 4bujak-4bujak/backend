package com.example.sabujak.space.repository.meetingroom;

import com.example.sabujak.space.entity.MeetingRoom;

import java.util.List;

public interface MeetingRoomRepositoryCustom {
    List<MeetingRoom> findMeetingRoomList(String branchName, int roomCapacity, boolean projectorExists, boolean canVideoConference, boolean isPrivate, String sortTarget, String sortDirection);
}
