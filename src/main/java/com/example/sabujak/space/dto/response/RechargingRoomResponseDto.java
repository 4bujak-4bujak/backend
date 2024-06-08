package com.example.sabujak.space.dto.response;

import com.example.sabujak.space.entity.RechargingRoom;
import lombok.Getter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RechargingRoomResponseDto {

    @Getter
    public static class RechargingRoomForList {
        private Long rechargingRoomId;
        private String rechargingRoomName;
        private int rechargingRoomFloor;
        private List<ReservationTimes> times;

        public static RechargingRoomForList of(RechargingRoom rechargingRoom, Map<String, Boolean> reservationTimes) {
            RechargingRoomForList rechargingRoomForList = new RechargingRoomForList();
            rechargingRoomForList.rechargingRoomId = rechargingRoom.getSpaceId();
            rechargingRoomForList.rechargingRoomName = rechargingRoom.getSpaceName();
            rechargingRoomForList.rechargingRoomFloor = rechargingRoom.getSpaceFloor();
            rechargingRoomForList.times = reservationTimes.entrySet().stream()
                    .map(entry -> ReservationTimes.of(entry.getKey(), entry.getValue()))
                    .sorted(Comparator.comparing(ReservationTimes::getStartAt))
                    .toList();
            return rechargingRoomForList;
        }

    }

    @Getter
    public static class ReservationTimes {
        private String startAt;
        private String endAt;
        private boolean canReserve;

        public static ReservationTimes of(String startAt, Boolean canReserve) {
            ReservationTimes reservationTimes = new ReservationTimes();
            reservationTimes.startAt = startAt;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime start = LocalTime.parse(startAt, formatter);
            LocalTime endAt = start.plusMinutes(30);
            reservationTimes.endAt = endAt.format(formatter);

            reservationTimes.canReserve = canReserve;
            return reservationTimes;
        }
    }
}
