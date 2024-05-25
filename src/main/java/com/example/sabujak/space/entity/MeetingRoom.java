package com.example.sabujak.space.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "meeting_room")
public class MeetingRoom extends Space {

    @Column(name = "meeting_room_capacity")
    @Positive
    private int meetingRoomCapacity;

    @Column(name = "projector_exists")
    @NotNull
    private boolean projectorExists;

    @Column(name = "can_video_conference")
    @NotNull
    private boolean canVideoConference;

    @Column(name = "isPrivate")
    @NotNull
    private boolean isPrivate;
}
