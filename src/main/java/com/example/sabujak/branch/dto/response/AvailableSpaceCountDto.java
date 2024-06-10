package com.example.sabujak.branch.dto.response;

public record AvailableSpaceCountDto(
        int totalMeetingRoomCount,
        int availableMeetingRoomCount,
        int totalRechargingRoomCount,
        int availableRechargingRoomCount,
        int totalFocusDeskCount,
        int availableFocusDeskCount) {
}
