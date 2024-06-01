package com.example.sabujak.space.dto.response;

public class FocusDeskResponseDto {

    public record AvailableSeatCountInformation(int totalSeatCount, int availableSeatCount, int reservedSeatCount) {
    }
}
