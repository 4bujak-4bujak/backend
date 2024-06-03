package com.example.sabujak.branch.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BranchWithSpaceDto {
    private Long id;
    private String branchName;
    private String branchAddress;

    private int branchTotalMeetingRoomCount; // 전체 회의실
    private int branchActiveMeetingRoomCount; // 사용중인 회의실


}
