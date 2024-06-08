package com.example.sabujak.space.service;

import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.branch.exception.BranchErrorCode;
import com.example.sabujak.branch.exception.BranchException;
import com.example.sabujak.branch.repository.BranchRepository;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.reservation.entity.Reservation;
import com.example.sabujak.reservation.repository.ReservationRepository;
import com.example.sabujak.security.exception.AuthException;
import com.example.sabujak.space.dto.SpaceCountResponseDto;
import com.example.sabujak.space.dto.response.FocusDeskResponseDto;
import com.example.sabujak.space.dto.response.MeetingRoomResponseDto;
import com.example.sabujak.space.dto.response.RechargingRoomResponseDto;
import com.example.sabujak.common.dto.ToastType;
import com.example.sabujak.space.entity.MeetingRoom;
import com.example.sabujak.space.entity.MeetingRoomType;
import com.example.sabujak.space.entity.RechargingRoom;
import com.example.sabujak.space.entity.Space;
import com.example.sabujak.space.exception.meetingroom.SpaceException;
import com.example.sabujak.space.repository.FocusDeskRepository;

import com.example.sabujak.space.repository.RechargingRoomRepository;
import com.example.sabujak.space.repository.SpaceRepository;

import com.example.sabujak.space.repository.meetingroom.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.sabujak.security.exception.AuthErrorCode.ACCOUNT_NOT_EXISTS;
import static com.example.sabujak.space.exception.meetingroom.SpaceErrorCode.MEETING_ROOM_NOT_FOUND;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SpaceService {
    private final MeetingRoomRepository meetingRoomRepository;
    private final FocusDeskRepository focusDeskRepository;
    private final RechargingRoomRepository rechargingRoomRepository;
    private final BranchRepository branchRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final SpaceRepository spaceRepository;

    public MeetingRoomResponseDto.MeetingRoomList getMeetingRoomList(String email, LocalDateTime startAt, LocalDateTime endAt,
                                                                     String branchName, List<MeetingRoomType> roomTypes, boolean projectorExists,
                                                                     boolean canVideoConference, boolean isPrivate, String sortTarget, String sortDirection) {
        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        ToastType toastType = null;

        if (verifyOverlappingMeetingRoom(member, startAt, endAt)) {
            toastType = ToastType.OVERLAPPING_MEETING_ROOM_EXISTS;
        } else if (verifyOverlappingRechargingRoom(member, startAt, endAt)) {
            toastType = ToastType.OVERLAPPING_RECHARGING_ROOM_EXISTS;
        }

        return new MeetingRoomResponseDto.MeetingRoomList(
                meetingRoomRepository.findMeetingRoomList(startAt, endAt, branchName, roomTypes, projectorExists, canVideoConference, isPrivate, sortTarget, sortDirection)
                        .stream()
                        .map(MeetingRoomResponseDto.MeetingRoomForList::from)
                        .collect(Collectors.toList()),
                toastType);
    }

    private boolean verifyOverlappingMeetingRoom(Member member, LocalDateTime startAt, LocalDateTime endAt) {
        if (reservationRepository.existsOverlappingMeetingRoomReservation(member, startAt, endAt)) {
            return true;
        }
        return false;
    }

    private boolean verifyOverlappingRechargingRoom(Member member, LocalDateTime startAt, LocalDateTime endAt) {
        if (reservationRepository.existsOverlappingRechargingRoomReservationByStartAt(member, startAt, endAt)) {
            return true;
        }
        return false;
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

    public List<RechargingRoomResponseDto.RechargingRoomForList> getRechargingRoomList(Long branchId) {
        List<RechargingRoomResponseDto.RechargingRoomForList> rechargingRoomList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        int minutes = now.getMinute();
        if (minutes < 30) {
            now = now.withMinute(30).withSecond(0).withNano(0);
        } else {
            now = now.withMinute(0).withSecond(0).withNano(0).plusHours(1);
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchException(BranchErrorCode.BRANCH_NOT_FOUND));

        List<RechargingRoom> rechargingRooms = rechargingRoomRepository.findAllByBranch(branch);
        List<Reservation> reservations = reservationRepository.findAllByRechargingRoomListAndStartTimes(rechargingRooms, now, now.plusHours(2));
        Map<Space, List<Reservation>> reservationMap = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getSpace));

        for (RechargingRoom rechargingRoom : rechargingRooms) {
            Map<String, Boolean> reservationTimes = new HashMap<>();
            for (int i = 0; i < 5; i++) {
                String startTime = now.plusMinutes(30 * i).format(DateTimeFormatter.ofPattern("HH:mm"));
                String endTime = now.plusMinutes(30 * (i + 1)).format(DateTimeFormatter.ofPattern("HH:mm"));

                reservationTimes.put(startTime, true);
            }

            if (reservationMap.size() > 0) {
                for (Reservation reservation : reservationMap.get(rechargingRoom)) {
                    reservationTimes.put(reservation.getReservationStartDateTime().format(DateTimeFormatter.ofPattern("HH:mm")), false);
                }
            }

            rechargingRoomList.add(RechargingRoomResponseDto.RechargingRoomForList.of(rechargingRoom, reservationTimes));
        }
        return rechargingRoomList;
    }
      
    public SpaceCountResponseDto countRoomByBranch(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchException(BranchErrorCode.BRANCH_NOT_FOUND));
        int miniCount = meetingRoomRepository.countAllByBranchAndMeetingRoomType(branch, MeetingRoomType.MINI);
        int standardCount = meetingRoomRepository.countAllByBranchAndMeetingRoomType(branch, MeetingRoomType.STANDARD);
        int mediumCount = meetingRoomRepository.countAllByBranchAndMeetingRoomType(branch, MeetingRoomType.MEDIUM);
        int stateCount = meetingRoomRepository.countAllByBranchAndMeetingRoomType(branch, MeetingRoomType.STATE);

        int rechargingCount = spaceRepository.countAllRechargingRoomByBranch(branchId);
        int focusCount = focusDeskRepository.countAllByBranch(branch);

        return new SpaceCountResponseDto(
                miniCount,
                standardCount,
                mediumCount,
                stateCount,
                rechargingCount,
                focusCount
        );
    }
}
