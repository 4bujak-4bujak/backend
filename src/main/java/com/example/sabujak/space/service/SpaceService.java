package com.example.sabujak.space.service;

import com.example.sabujak.space.dto.response.MeetingRoomResponseDto;
import com.example.sabujak.space.entity.MeetingRoom;
import com.example.sabujak.space.entity.MeetingRoomType;
import com.example.sabujak.space.exception.meetingroom.MeetingRoomException;
import com.example.sabujak.space.repository.meetingroom.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.sabujak.space.exception.meetingroom.MeetingRoomErrorCode.MEETING_ROOM_NOT_FOUND;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SpaceService {
    private final MeetingRoomRepository meetingRoomRepository;

    public List<MeetingRoomResponseDto.MeetingRoomForList> getMeetingRoomList(LocalDateTime startAt, LocalDateTime endAt,
                                                                              String branchName, List<MeetingRoomType> roomTypes, boolean projectorExists,
                                                                              boolean canVideoConference, boolean isPrivate, String sortTarget, String sortDirection) {

        return meetingRoomRepository.findMeetingRoomList(startAt, endAt, branchName, roomTypes, projectorExists, canVideoConference, isPrivate, sortTarget, sortDirection)
                .stream()
                .map(MeetingRoomResponseDto.MeetingRoomForList::from)
                .collect(Collectors.toList());
    }

    public MeetingRoomResponseDto.MeetingRoomDetails getMeetingRoomDetails(Long meetingRoomId) {
        MeetingRoom meetingRoom = meetingRoomRepository.findByMeetingRoomIdWithBranch(meetingRoomId)
                .orElseThrow(() -> new MeetingRoomException(MEETING_ROOM_NOT_FOUND));
        return MeetingRoomResponseDto.MeetingRoomDetails.of(meetingRoom.getBranch(), meetingRoom);
    }
}
