package com.example.sabujak.space.dto;

import lombok.AllArgsConstructor;


public record SpaceCountResponseDto(
        int miniRoomCount,
        int standardRoomCount,
        int mediumRoomCount,
        int stateRoomCount,
        int rechargingRoomCount,
        int focusDeskCount) {

}
