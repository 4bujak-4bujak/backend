package com.example.sabujak.reservation.service;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.reservation.dto.request.ReservationRequestDto;
import com.example.sabujak.reservation.dto.response.ReservationResponseDto;
import com.example.sabujak.reservation.entity.Reservation;
import com.example.sabujak.reservation.exception.ReservationException;
import com.example.sabujak.reservation.repository.ReservationRepository;
import com.example.sabujak.security.exception.AuthException;
import com.example.sabujak.space.entity.FocusDesk;
import com.example.sabujak.space.entity.MeetingRoom;
import com.example.sabujak.space.exception.meetingroom.SpaceException;
import com.example.sabujak.space.repository.FocusDeskRepository;
import com.example.sabujak.space.repository.meetingroom.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.sabujak.reservation.exception.ReservationErrorCode.ALREADY_RESERVED_FOCUS_DESK;
import static com.example.sabujak.reservation.exception.ReservationErrorCode.REPRESENTATIVE_OVERLAPPING_MEETINGROOM_EXISTS;
import static com.example.sabujak.security.exception.AuthErrorCode.ACCOUNT_NOT_EXISTS;
import static com.example.sabujak.space.exception.meetingroom.SpaceErrorCode.FOCUS_DESK_NOT_FOUND;
import static com.example.sabujak.space.exception.meetingroom.SpaceErrorCode.MEETING_ROOM_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final FocusDeskRepository focusDeskRepository;

//    public List<ReservationResponseDto.FindMember> findMembers(String email, String searchTerm) {
//        final Member member = memberRepository.findWithCompanyAndImageByMemberEmail(email)
//                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));
//
//        return memberRepository.findMemberCanInvite(member.getCompany(), member.getMemberId(), searchTerm).stream()
//                .map(ReservationResponseDto.FindMember::from)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional
//    public void reserveMeetingRoom(String email, ReservationRequestDto.MeetingRoomDto meetingRoomDto) {
//        final Member representative = memberRepository.findByMemberEmail(email)
//                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));
//
//        final MeetingRoom meetingRoom = meetingRoomRepository.findById(meetingRoomDto.meetingRoomId())
//                .orElseThrow(() -> new SpaceException(MEETING_ROOM_NOT_FOUND));
//
//        final List<Member> participants = memberRepository.findByMemberIdIn(meetingRoomDto.memberIds());
//
//        //대표자 미팅룸 예약 검증
//        if (verifyOverlappingMeetingRoom(representative, meetingRoomDto.startAt(), meetingRoomDto.endAt())) {
//            throw new ReservationException(REPRESENTATIVE_OVERLAPPING_MEETINGROOM_EXISTS);
//        }
//        //참여자 미팅룸 예약 검증
//        else if (verifyOverlappingMeetingRoom(participants, meetingRoomDto.startAt(), meetingRoomDto.endAt())) {
//            throw new ReservationException(REPRESENTATIVE_OVERLAPPING_MEETINGROOM_EXISTS);
//        }
//
//        Reservation reservation = meetingRoomDto.toReservationEntity(meetingRoom, meetingRoomDto.startAt(), meetingRoomDto.endAt(), representative, participants);
//
//
//        reservationRepository.save(reservation);
//    }
//
//    private boolean verifyOverlappingMeetingRoom(Member representative, LocalDateTime startAt, LocalDateTime endAt) {
//        if (reservationRepository.existsOverlappingMeetingRoomReservation(representative, startAt, endAt)) {
//            return true;
//        }
//        return false;
//    }
//
//    private boolean verifyOverlappingMeetingRoom(List<Member> participants, LocalDateTime startAt, LocalDateTime endAt) {
//        for (Member participant : participants) {
//            if (reservationRepository.existsOverlappingMeetingRoomReservation(participant, startAt, endAt)) {
//                return true;
//            }
//        }
//        return false;
//    }

    @Transactional
    public void reserveFocusDesk(String email, ReservationRequestDto.FocusDeskDto focusDeskDto) {
        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        FocusDesk focusDesk = focusDeskRepository.findById(focusDeskDto.focusDeskId())
                .orElseThrow(() -> new SpaceException(FOCUS_DESK_NOT_FOUND));

        if (!focusDesk.isCanReserve()) {
            throw new ReservationException(ALREADY_RESERVED_FOCUS_DESK);
        }

        List<Reservation> overlappingFocusDeskReservations = reservationRepository.findOverlappingFocusDeskReservation(member, focusDeskDto.startAt());
        if (!overlappingFocusDeskReservations.isEmpty()) {
            Reservation overlappedReservation = overlappingFocusDeskReservations.get(overlappingFocusDeskReservations.size() - 1);

            overlappedReservation.endUse(focusDeskDto.startAt());
            FocusDesk overlappedReservationSpace = (FocusDesk) overlappedReservation.getSpace();
            overlappedReservationSpace.changeCanReserve(true);
        }

        Reservation reservation = focusDeskDto.toReservationEntity(focusDesk, focusDeskDto.startAt(), member);
        focusDesk.changeCanReserve(false);

        reservationRepository.save(reservation);
    }
}
