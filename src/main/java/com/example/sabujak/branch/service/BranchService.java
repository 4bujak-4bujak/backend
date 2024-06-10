package com.example.sabujak.branch.service;

import com.example.sabujak.branch.dto.response.AvailableSpaceCountDto;
import com.example.sabujak.branch.dto.response.BranchDistanceResponseDto;
import com.example.sabujak.branch.dto.response.BranchResponseDto;
import com.example.sabujak.branch.dto.response.BranchWithSpaceDto;
import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.branch.exception.BranchException;
import com.example.sabujak.branch.repository.BranchRepository;
import com.example.sabujak.space.repository.SpaceRepository;
import com.example.sabujak.space.repository.meetingroom.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.example.sabujak.branch.exception.BranchErrorCode.BRANCH_NOT_FOUND;
import static com.example.sabujak.branch.exception.BranchErrorCode.ENTITY_NOT_FOUND_BY_NAME;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BranchService {

    private static final int MAX_SEARCH_COUNT = 2;
    private final BranchRepository branchRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final SpaceRepository spaceRepository;

    public BranchResponseDto findByBranchName(String branchName) {
        log.info("[BranchService findByBranchName] branchName: {}", branchName);
        return branchRepository.findByBranchName(branchName)
                .map(BranchResponseDto::fromEntity).orElseThrow(() -> new BranchException(ENTITY_NOT_FOUND_BY_NAME));
    }

    public List<BranchResponseDto> findAll() {
        log.info("[BranchService findAll]");
        return branchRepository.findAll()
                .stream().map(BranchResponseDto::fromEntity).toList();
    }

    public BranchWithSpaceDto getBranchWithSpace(LocalDateTime now, String branchName) {
        log.info("[BranchService getBranchWithSpace] branchName: {}, time: {}", branchName, now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Branch branch = branchRepository.findByBranchName(branchName).orElseThrow(() -> new BranchException(ENTITY_NOT_FOUND_BY_NAME));

        int branchTotalMeetingRoomCount = meetingRoomRepository.countTotalMeetingRoom(branchName);
        int branchActiveMeetingRoomCount = meetingRoomRepository.countActiveMeetingRoom(now, branchName);

        return new BranchWithSpaceDto(branch.getBranchId(), branch.getBranchName(), branch.getBranchAddress(), branchTotalMeetingRoomCount, branchActiveMeetingRoomCount);
    }

    public List<BranchDistanceResponseDto> getNearBranchByPosition(double latitude, double longitude) {
        log.info("[BranchService getNearBranch] latitude: {}, longitude: {}", latitude, longitude);

        return branchRepository.findAll().stream().sorted((a, b) -> {
                            double calA = calculateDistance(latitude, longitude, a.getBranchLatitude(), a.getBranchLongitude());
                            double calB = calculateDistance(latitude, longitude, b.getBranchLatitude(), b.getBranchLongitude());
                            if (calA < calB)
                                return -1;
                            else if (calA > calB)
                                return 1;
                            return 0;
                        }
                ).map(branch -> new BranchDistanceResponseDto(
                        branch.getBranchId(),
                        branch.getBranchName(),
                        calculateDistance(latitude, longitude, branch.getBranchLatitude(), branch.getBranchLongitude())))
                .limit(MAX_SEARCH_COUNT).toList();

    }


    // Haversine formula -> 두 좌표간 거리 계산
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }


    public AvailableSpaceCountDto getAvailableSpaceCount(Long branchId) {
        LocalDateTime now = LocalDateTime.now();
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchException(BRANCH_NOT_FOUND));

        List<Long> allCounts = spaceRepository.countAllSpaceByBranch(branch);
        Long focusDeskTotalCount = allCounts.get(0);
        Long meetingRoomTotalCount = allCounts.get(1);
        Long rechargingRoomTotalCount = allCounts.get(2);

        Long focusDeskUsingCount = spaceRepository.countUsingSpaceByBranchAndDtype(branch, now, "FocusDesk");
        Long meetingRoomUsingCount = spaceRepository.countUsingSpaceByBranchAndDtype(branch, now, "MeetingRoom");
        Long rechargingRoomUsingCount = spaceRepository.countUsingSpaceByBranchAndDtype(branch, now, "RechargingRoom");

        return new AvailableSpaceCountDto(Math.toIntExact(meetingRoomTotalCount), Math.toIntExact(meetingRoomTotalCount - meetingRoomUsingCount),
                Math.toIntExact(rechargingRoomTotalCount), Math.toIntExact(rechargingRoomTotalCount - rechargingRoomUsingCount),
                Math.toIntExact(focusDeskTotalCount), Math.toIntExact(focusDeskTotalCount - focusDeskUsingCount));
    }
}
