package com.example.sabujak.space.repository.meetingroom;

import com.example.sabujak.space.entity.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long>, MeetingRoomRepositoryCustom {
}
