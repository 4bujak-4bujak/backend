package com.example.sabujak.space.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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


    //이제 여기에 비품들 들어감
}
