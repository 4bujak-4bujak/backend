package com.example.sabujak.space.service;

import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.branch.exception.BranchErrorCode;
import com.example.sabujak.branch.exception.BranchException;
import com.example.sabujak.branch.repository.BranchRepository;
import com.example.sabujak.space.dto.response.FocusDeskResponseDto;
import com.example.sabujak.space.dto.response.MeetingRoomResponseDto;
import com.example.sabujak.space.entity.MeetingRoom;
import com.example.sabujak.space.entity.MeetingRoomType;
import com.example.sabujak.space.exception.meetingroom.SpaceException;
import com.example.sabujak.space.repository.FocusDeskRepository;
import com.example.sabujak.space.repository.meetingroom.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.sabujak.space.exception.meetingroom.SpaceErrorCode.MEETING_ROOM_NOT_FOUND;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SpaceService {
    private final MeetingRoomRepository meetingRoomRepository;
    private final FocusDeskRepository focusDeskRepository;
    private final BranchRepository branchRepository;

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
                .orElseThrow(() -> new SpaceException(MEETING_ROOM_NOT_FOUND));
        return MeetingRoomResponseDto.MeetingRoomDetails.of(meetingRoom.getBranch(), meetingRoom);
    }

    public FocusDeskResponseDto.AvailableSeatCountInformation getAvailableSeatCount(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchException(BranchErrorCode.BRANCH_NOT_FOUND));

        Integer totalCount = focusDeskRepository.countAllByBranch(branch);
        Integer availableCount = focusDeskRepository.countAllByBranchAndAndCanReserve(branch, true);
        Integer reservedCount = totalCount - availableCount;

        return new FocusDeskResponseDto.AvailableSeatCountInformation(totalCount, availableCount, reservedCount);
    }

    public List<FocusDeskResponseDto.FocusDeskForList> getFocusDeskList(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchException(BranchErrorCode.BRANCH_NOT_FOUND));

        return focusDeskRepository.findAllByBranch(branch)
                .stream()
                .map(FocusDeskResponseDto.FocusDeskForList::from)
                .collect(Collectors.toList());
    }
}
