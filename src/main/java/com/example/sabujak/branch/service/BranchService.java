package com.example.sabujak.branch.service;
import com.example.sabujak.branch.dto.response.BranchResponseDto;
import com.example.sabujak.branch.dto.response.BranchWithSpaceDto;
import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.branch.exception.BranchException;
import com.example.sabujak.branch.repository.BranchRepository;
import com.example.sabujak.space.repository.meetingroom.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.example.sabujak.branch.exception.BranchErrorCode.ENTITY_NOT_FOUND_BY_NAME;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BranchService {

    private final BranchRepository branchRepository;
    private final MeetingRoomRepository meetingRoomRepository;

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

        return new BranchWithSpaceDto(branch.getBranchId(), branch.getBranchName(),branch.getBranchAddress(),branchTotalMeetingRoomCount,branchActiveMeetingRoomCount);
    }



}
