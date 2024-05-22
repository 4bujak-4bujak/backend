package com.example.sabujak.space.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "meeting_room")
public class MeetingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_room_id")
    private Long meetingRoomId;

    @Column(name = "meeting_room_capacity")
    @Positive
    private int meetingRoomCapacity;


    //이제 여기에 비품들 들어감
}
