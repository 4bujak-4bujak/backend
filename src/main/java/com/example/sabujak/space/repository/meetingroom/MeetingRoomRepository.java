package com.example.sabujak.space.repository.meetingroom;

import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.space.entity.MeetingRoom;
import com.example.sabujak.space.entity.MeetingRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long>, MeetingRoomRepositoryCustom {

    @Query("SELECT m FROM MeetingRoom m LEFT JOIN FETCH m.branch WHERE m.spaceId = :meetingRoomId")
    Optional<MeetingRoom> findByMeetingRoomIdWithBranch(Long meetingRoomId);

    Integer countAllByBranchAndMeetingRoomType(Branch branch, MeetingRoomType meetingRoomType);
}
