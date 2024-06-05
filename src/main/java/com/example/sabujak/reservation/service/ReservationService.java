package com.example.sabujak.reservation.service;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.reservation.dto.request.ReservationRequestDto;
import com.example.sabujak.reservation.dto.response.ReservationHistoryResponse;
import com.example.sabujak.reservation.dto.response.ReservationResponseDto;
import com.example.sabujak.reservation.entity.MemberReservation;
import com.example.sabujak.reservation.entity.MemberReservationType;
import com.example.sabujak.reservation.entity.Reservation;
import com.example.sabujak.reservation.exception.ReservationException;
import com.example.sabujak.reservation.repository.MemberReservationRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.sabujak.reservation.exception.ReservationErrorCode.*;
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
    private final MemberReservationRepository memberReservationRepository;

    //    public List<ReservationResponseDto.FindMember> findMembers(String email, String searchTerm) {
//        final Member member = memberRepository.findWithCompanyAndImageByMemberEmail(email)
//                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));
//
//        return memberRepository.findMemberCanInvite(member.getCompany(), member.getMemberId(), searchTerm).stream()
//                .map(ReservationResponseDto.FindMember::from)
//                .collect(Collectors.toList());
//    }
//
    @Transactional
    public void reserveMeetingRoom(String email, ReservationRequestDto.MeetingRoomDto meetingRoomDto) {
        final Member representative = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        final MeetingRoom meetingRoom = meetingRoomRepository.findById(meetingRoomDto.meetingRoomId())
                .orElseThrow(() -> new SpaceException(MEETING_ROOM_NOT_FOUND));

        final List<Member> participants = memberRepository.findByMemberIdIn(meetingRoomDto.memberIds());

        //대표자 미팅룸 예약 검증
        if (verifyOverlappingMeetingRoom(representative, meetingRoomDto.startAt(), meetingRoomDto.endAt())) {
            throw new ReservationException(REPRESENTATIVE_OVERLAPPING_MEETINGROOM_EXISTS);
        }
        //참여자 미팅룸 예약 검증
        else if (verifyOverlappingMeetingRoom(participants, meetingRoomDto.startAt(), meetingRoomDto.endAt())) {
            throw new ReservationException(REPRESENTATIVE_OVERLAPPING_MEETINGROOM_EXISTS);
        }

        Reservation reservation = meetingRoomDto.toReservationEntity(meetingRoom, representative, participants);


        reservationRepository.save(reservation);
    }

    private boolean verifyOverlappingMeetingRoom(Member representative, LocalDateTime startAt, LocalDateTime endAt) {
        if (reservationRepository.existsOverlappingMeetingRoomReservation(representative, startAt, endAt)) {
            return true;
        }
        return false;
    }

    private boolean verifyOverlappingMeetingRoom(List<Member> participants, LocalDateTime startAt, LocalDateTime endAt) {
        for (Member participant : participants) {
            if (reservationRepository.existsOverlappingMeetingRoomReservation(participant, startAt, endAt)) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void reserveFocusDesk(String email, ReservationRequestDto.FocusDeskDto focusDeskDto) {

        LocalDateTime now = LocalDateTime.now();

        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        FocusDesk focusDesk = focusDeskRepository.findById(focusDeskDto.focusDeskId())
                .orElseThrow(() -> new SpaceException(FOCUS_DESK_NOT_FOUND));

        // 이미 예약된 포커스 데스크인지 확인
        if (!focusDesk.isCanReserve()) {
            throw new ReservationException(ALREADY_RESERVED_FOCUS_DESK);
        }

        // 해당 회원이 당일 예약한 포커스 데스크를 시간순으로 가져옴
        List<Reservation> todayReservations = reservationRepository.findTodayFocusDeskReservationOrderByTime(member, now);

        // 당일 예약한게 있는데 해당 좌석을 예약 종료하지 않았으면 기존 좌석 사용 종료
        if (!todayReservations.isEmpty()) {

            // 당일 예약 중 가장 최근 좌석을 찾아서
            Reservation todayLatestReservation = todayReservations.get(todayReservations.size() - 1);
            FocusDesk todayLatestFocusDesk = (FocusDesk) todayLatestReservation.getSpace();

            // 해당 좌석을 예약 종료 하지 않았으면 사용 종료
            if (todayLatestReservation.getReservationEndDateTime().isAfter(now) && !todayLatestFocusDesk.isCanReserve()) {
                todayLatestReservation.endUse(now);
                todayLatestFocusDesk.changeCanReserve(true);
            }
        }

        //새로운 좌석 예약
        Reservation reservation = focusDeskDto.toReservationEntity(focusDesk, now, member);
        focusDesk.changeCanReserve(false);

        reservationRepository.save(reservation);
    }

    @Transactional
    public void endUseFocusDesk(String email, ReservationRequestDto.FocusDeskDto focusDeskDto) {

        LocalDateTime now = LocalDateTime.now();

        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        FocusDesk focusDesk = focusDeskRepository.findById(focusDeskDto.focusDeskId())
                .orElseThrow(() -> new SpaceException(FOCUS_DESK_NOT_FOUND));

        // 해당 회원이 당일 예약한 포커스 데스크를 시간순으로 가져옴
        List<Reservation> todayReservations = reservationRepository.findTodayFocusDeskReservationOrderByTime(member, now);

        // 당일 예약한게 없으면 요청 좌석이 해당 회원이 예약한 좌석이 아니니까 예외처리
        if (todayReservations.isEmpty()) {
            throw new ReservationException(NOT_RESERVED_BY_MEMBER);
        }

        // 당일 예약 중 가장 최근 좌석을 찾아서 예약 종료 요청한 좌석이랑 맞는지 검증
        Reservation todayLatestReservation = todayReservations.get(todayReservations.size() - 1);
        FocusDesk todayLatestFocusDesk = (FocusDesk) todayLatestReservation.getSpace();
        if (todayLatestFocusDesk.getFocusDeskNumber() != focusDesk.getFocusDeskNumber()) {
            throw new ReservationException(NOT_RESERVED_BY_MEMBER);
        }

        // 이미 예약 종료를 한 내역인지 확인
        if (todayLatestReservation.getReservationEndDateTime().isBefore(now) && focusDesk.isCanReserve()) {
            throw new ReservationException(ALREADY_ENDED_RESERVED_FOCUS_DESK);
        }

        //모든 검증을 통과하면 사용 종료
        todayLatestReservation.endUse(now);
        focusDesk.changeCanReserve(true);
    }

    public ReservationResponseDto.CheckOverlap checkOverlap(String email, Long focusDeskId) {

        LocalDateTime now = LocalDateTime.now();

        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        FocusDesk focusDesk = focusDeskRepository.findById(focusDeskId)
                .orElseThrow(() -> new SpaceException(FOCUS_DESK_NOT_FOUND));

        // 해당 회원이 당일 예약한 포커스 데스크를 시간순으로 가져옴
        List<Reservation> todayReservations = reservationRepository.findTodayFocusDeskReservationOrderByTime(member, now);

        // 당일 예약한게 없으면 사용중인 좌석이 없음
        if (todayReservations.isEmpty()) {
            return new ReservationResponseDto.CheckOverlap(false);
        }

        // 당일 예약 중 가장 최근 좌석을 찾아서
        Reservation todayLatestReservation = todayReservations.get(todayReservations.size() - 1);
        FocusDesk todayLatestFocusDesk = (FocusDesk) todayLatestReservation.getSpace();

        // 가장 최근 좌석을 종료하지 않았고 해당 좌석이 예약 종료된 상태가 아니면 사용중인 좌석이 있음
        if (todayLatestReservation.getReservationEndDateTime().isAfter(now) && !todayLatestFocusDesk.isCanReserve()) {
            return new ReservationResponseDto.CheckOverlap(true);
        }

        return new ReservationResponseDto.CheckOverlap(false);
    }

    public ReservationHistoryResponse.TodayReservationCount getTodayReservationCount(String email) {

        LocalDateTime now = LocalDateTime.now();

        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        Integer todayReservationCount = reservationRepository.countTodayReservation(member, now);

        return new ReservationHistoryResponse.TodayReservationCount(todayReservationCount);
    }

    public List<ReservationHistoryResponse.ReservationForList> getReservations(String email, int durationStart, int durationEnd) {
        List<ReservationHistoryResponse.ReservationForList> reservationForLists = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        List<Reservation> todayReservations = reservationRepository.findReservationsWithDuration(member, now, durationStart, durationEnd);
        List<MemberReservation> memberReservations = memberReservationRepository.findMemberReservationsByReservations(todayReservations);

        Map<Reservation, List<MemberReservation>> memberReservationMap = memberReservations.stream()
                .collect(Collectors.groupingBy(MemberReservation::getReservation));

        for (Reservation reservation : todayReservations) {
            List<MemberReservation> memberReservationsInReservation = memberReservationMap.get(reservation);

            Optional<MemberReservationType> memberType = memberReservationsInReservation.stream()
                    .filter(memberReservation -> memberReservation.getMember().getMemberId().equals(member.getMemberId()))
                    .map(MemberReservation::getMemberReservationType)
                    .findFirst();

            reservationForLists.add(ReservationHistoryResponse.ReservationForList.of(
                    reservation,
                    reservation.getSpace(),
                    memberReservationsInReservation.stream()
                            .map(MemberReservation::getMember)
                            .collect(Collectors.toList()),
                    memberType.orElse(null)));
        }

        return reservationForLists;
    }
}
