package com.example.sabujak.space.service;

import com.example.sabujak.space.dto.response.SpaceResponseDto;
import com.example.sabujak.space.repository.meetingroom.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class SpaceService {
    private final MeetingRoomRepository meetingRoomRepository;

    public List<SpaceResponseDto.MeetingRoomDto> getMeetingRoomList(String branchName, int roomCapacity, boolean projectorExists,
                                                                    boolean canVideoConference, boolean isPrivate, String sortTarget, String sortDirection) {

        return meetingRoomRepository.findMeetingRoomList(branchName, roomCapacity, projectorExists, canVideoConference, isPrivate, sortTarget, sortDirection)
                .stream()
                .map(SpaceResponseDto.MeetingRoomDto::from)
                .collect(Collectors.toList());
    }
}
