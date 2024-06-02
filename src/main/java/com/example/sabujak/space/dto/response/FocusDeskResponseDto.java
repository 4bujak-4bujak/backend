package com.example.sabujak.space.dto.response;

import com.example.sabujak.space.entity.FocusDesk;

public class FocusDeskResponseDto {

    public record AvailableSeatCountInformation(int totalSeatCount, int availableSeatCount, int reservedSeatCount) {
    }

    public record FocusDeskForList(Long focusDeskId, int focusDeskNumber, boolean canReserve) {

        public static FocusDeskForList from(FocusDesk focusDesk) {
            return new FocusDeskForList(focusDesk.getSpaceId(), focusDesk.getFocusDeskNumber(), focusDesk.isCanReserve());
        }
    }
}
