package com.example.sabujak.space.service;

import com.example.sabujak.space.dto.response.SpaceResponseDto;
import com.example.sabujak.space.entity.MeetingRoomType;
import com.example.sabujak.space.repository.meetingroom.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SpaceService {
    private final MeetingRoomRepository meetingRoomRepository;

    public List<SpaceResponseDto.MeetingRoomDto> getMeetingRoomList(LocalDateTime startAt, LocalDateTime endAt,
                                                                    String branchName, List<MeetingRoomType> roomTypes, boolean projectorExists,
                                                                    boolean canVideoConference, boolean isPrivate, String sortTarget, String sortDirection) {

        return meetingRoomRepository.findMeetingRoomList(startAt, endAt, branchName, roomTypes, projectorExists, canVideoConference, isPrivate, sortTarget, sortDirection)
                .stream()
                .map(SpaceResponseDto.MeetingRoomDto::from)
                .collect(Collectors.toList());
    }
}
