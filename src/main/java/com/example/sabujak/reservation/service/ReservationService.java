package com.example.sabujak.reservation.service;

import com.example.sabujak.common.dto.ToastType;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.reservation.dto.event.CancelRechargingRoomNotification;
import com.example.sabujak.reservation.dto.event.FindMeetingRoomEntryNotificationMembersEvent;
import com.example.sabujak.reservation.dto.event.FindRechargingRoomEntryNotificationMemberEvent;
import com.example.sabujak.reservation.dto.event.ReserveMeetingRoomEvent;
import com.example.sabujak.reservation.dto.event.ReserveRechargingRoomEvent;
import com.example.sabujak.reservation.dto.request.ReservationRequestDto;
import com.example.sabujak.reservation.dto.response.ReservationHistoryResponse;
import com.example.sabujak.reservation.dto.response.ReservationProgress;
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
import com.example.sabujak.space.entity.RechargingRoom;
import com.example.sabujak.space.exception.meetingroom.SpaceException;
import com.example.sabujak.space.repository.FocusDeskRepository;
import com.example.sabujak.space.repository.RechargingRoomRepository;
import com.example.sabujak.space.repository.meetingroom.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.sabujak.notification.utils.NotificationContent.*;
import static com.example.sabujak.reservation.entity.ReservationStatus.ACCEPTED;
import static com.example.sabujak.reservation.entity.ReservationStatus.CANCELED;
import static com.example.sabujak.reservation.exception.ReservationErrorCode.*;
import static com.example.sabujak.security.exception.AuthErrorCode.ACCOUNT_NOT_EXISTS;
import static com.example.sabujak.space.exception.meetingroom.SpaceErrorCode.FOCUS_DESK_NOT_FOUND;
import static com.example.sabujak.space.exception.meetingroom.SpaceErrorCode.MEETING_ROOM_NOT_FOUND;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final FocusDeskRepository focusDeskRepository;
    private final RechargingRoomRepository rechargingRoomRepository;
    private final MemberReservationRepository memberReservationRepository;

    private final ApplicationEventPublisher publisher;

    public ReservationResponseDto.FindMemberList findMembers(String email, String searchTerm, LocalDateTime startAt, LocalDateTime endAt) {
        final Member member = memberRepository.findWithCompanyAndImageByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        List<Member> searchedMembers = memberRepository.searchMembers(member.getCompany(), member.getMemberId(), searchTerm);
        Set<Member> searchedMembersCantInvite = new HashSet<>(searchedMembers);
        List<Member> searchedMembersCanInvite = memberRepository.searchMembersCanInviteInMembers(searchedMembers, startAt, endAt);
        searchedMembersCanInvite.forEach(searchedMembersCantInvite::remove);

        return new ReservationResponseDto.FindMemberList(
                searchedMembersCanInvite.stream()
                        .map(ReservationResponseDto.FindMember::from)
                        .collect(Collectors.toList()),
                searchedMembersCantInvite.stream()
                        .map(ReservationResponseDto.FindMember::from)
                        .collect(Collectors.toSet()));
    }

    @Transactional
    public void reserveMeetingRoom(String email, ReservationRequestDto.MeetingRoomDto meetingRoomDto) {
        LocalDateTime now = LocalDateTime.now();
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
            throw new ReservationException(PARTICIPANTS_OVERLAPPING_MEETINGROOM_EXISTS);
        }

        // 대표자 및 참여자 리차징룸 중복 예약 처리
        List<Member> members = new ArrayList<>(participants);
        members.add(representative);

        List<Reservation> membersOverlappingRechargingRoomReservations = reservationRepository.findOverlappingRechargingRoomReservationInMembers(members, meetingRoomDto.startAt(), meetingRoomDto.endAt());
        List<CancelRechargingRoomNotification> cancelRechargingRoomNotifications = handleOverlappingRechargingRoomReservations(membersOverlappingRechargingRoomReservations, now);

        Reservation reservation = meetingRoomDto.toReservationEntity(meetingRoom, representative, participants);

        reservationRepository.save(reservation);

        publisher.publishEvent(createReserveMeetingRoomEvent(reservation, meetingRoom, participants, cancelRechargingRoomNotifications));
    }

    private boolean verifyOverlappingMeetingRoom(Member representative, LocalDateTime startAt, LocalDateTime endAt) {
        if (reservationRepository.existsOverlappingMeetingRoomReservation(representative, startAt, endAt)) {
            return true;
        }
        return false;
    }

    private boolean verifyOverlappingMeetingRoom(List<Member> participants, LocalDateTime startAt, LocalDateTime endAt) {
        if (reservationRepository.existsOverlappingMeetingRoomReservationInMembers(participants, startAt, endAt)) {
            return true;
        }
        return false;
    }

    private List<CancelRechargingRoomNotification> handleOverlappingRechargingRoomReservations(List<Reservation> overlappingRechargingRoomReservations, LocalDateTime now) {
        List<CancelRechargingRoomNotification> cancelRechargingRoomNotifications = new ArrayList<>();
        for (Reservation reservation : overlappingRechargingRoomReservations) {
            // 예약 시간이 겹치는데 이미 시작한 경우 종료 처리
            if (reservation.getReservationStartDateTime().isBefore(now)) {
                reservation.endUse(now);
            }
            // 겹치는데 시작 전인 경우 예약 취소 처리
            else {
                reservation.getMemberReservations().forEach(memberReservation -> {
                    memberReservation.cancelReservation();
                    cancelRechargingRoomNotifications.add(new CancelRechargingRoomNotification(
                            createRechargingRoomCancellationNotificationContent(reservation.getReservationStartDateTime()),
                            memberReservation.getMember()
                    ));
                });
            }
        }
        return cancelRechargingRoomNotifications;
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
    public void endUseFocusDesk(String email, Long spaceId) {

        LocalDateTime now = LocalDateTime.now();

        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        FocusDesk focusDesk = focusDeskRepository.findById(spaceId)
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

    public ReservationResponseDto.CheckFocusDeskOverlap checkFocusDeskOverlap(String email, Long focusDeskId) {

        LocalDateTime now = LocalDateTime.now();

        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        FocusDesk focusDesk = focusDeskRepository.findById(focusDeskId)
                .orElseThrow(() -> new SpaceException(FOCUS_DESK_NOT_FOUND));

        // 해당 회원이 당일 예약한 포커스 데스크를 시간순으로 가져옴
        List<Reservation> todayReservations = reservationRepository.findTodayFocusDeskReservationOrderByTime(member, now);

        // 당일 예약한게 없으면 사용중인 좌석이 없음
        if (todayReservations.isEmpty()) {
            return new ReservationResponseDto.CheckFocusDeskOverlap(false);
        }

        // 당일 예약 중 가장 최근 좌석을 찾아서
        Reservation todayLatestReservation = todayReservations.get(todayReservations.size() - 1);
        FocusDesk todayLatestFocusDesk = (FocusDesk) todayLatestReservation.getSpace();

        // 가장 최근 좌석을 종료하지 않았고 해당 좌석이 예약 종료된 상태가 아니면 사용중인 좌석이 있음
        if (todayLatestReservation.getReservationEndDateTime().isAfter(now) && !todayLatestFocusDesk.isCanReserve()) {
            return new ReservationResponseDto.CheckFocusDeskOverlap(true);
        }

        return new ReservationResponseDto.CheckFocusDeskOverlap(false);
    }

    public ReservationHistoryResponse.TodayReservationCount getTodayReservationCount(String email) {

        LocalDateTime now = LocalDateTime.now();

        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        Integer todayReservationCount = reservationRepository.countTodayReservation(member, now);

        return new ReservationHistoryResponse.TodayReservationCount(todayReservationCount);
    }

    public List<ReservationHistoryResponse.ReservationForList> getTodayReservations(String email) {
        List<ReservationHistoryResponse.ReservationForList> reservationForLists = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        List<Reservation> todayReservations = reservationRepository.findReservationsToday(member, now);
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

    public List<ReservationHistoryResponse.ReservationForList> getReservationsFor30Days(String email) {
        List<ReservationHistoryResponse.ReservationForList> reservationForLists = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        List<Reservation> reservations = reservationRepository.findReservationsWithDuration(member, now, 1, 30);
        List<MemberReservation> memberReservations = memberReservationRepository.findMemberReservationsByReservations(reservations);

        Map<Reservation, List<MemberReservation>> memberReservationMap = memberReservations.stream()
                .collect(Collectors.groupingBy(MemberReservation::getReservation));

        for (Reservation reservation : reservations) {
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

    public ReservationHistoryResponse.ReservationDetails getReservationDetails(String email, Long reservationId) {

        LocalDateTime now = LocalDateTime.now();

        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        Reservation reservation = reservationRepository.findByIdWithSpaceAndBranch(reservationId)
                .orElseThrow(() -> new ReservationException(RESERVATION_NOT_EXISTS));

        List<MemberReservation> memberReservations = memberReservationRepository.findByReservation(reservation);

        Member representative = null;
        List<Member> participants = new ArrayList<>();

        boolean checkMyReservation = false;

        for (MemberReservation memberReservation : memberReservations) {
            if (memberReservation.getMemberReservationType().equals(MemberReservationType.REPRESENTATIVE)) {
                representative = memberReservation.getMember();
            } else {
                participants.add(memberReservation.getMember());
            }

            if (memberReservation.getMember().getMemberId() == member.getMemberId()) {
                checkMyReservation = true;
            }
        }

        if (!checkMyReservation) {
            throw new ReservationException(NOT_RESERVED_BY_MEMBER);
        }

        MemberReservationType myMemberType = MemberReservationType.PARTICIPANT;
        if (representative.getMemberId() == member.getMemberId()) {
            myMemberType = MemberReservationType.REPRESENTATIVE;
        }

        ReservationProgress reservationProgress = null;

        if (reservation.getReservationStartDateTime().isAfter(now)) {
            reservationProgress = ReservationProgress.BEFORE_USE;
        } else if (reservation.getReservationStartDateTime().isBefore(now) && reservation.getReservationEndDateTime().isAfter(now)) {
            reservationProgress = ReservationProgress.IN_USE;
        } else {
            reservationProgress = ReservationProgress.AFTER_USE;
        }

        return ReservationHistoryResponse.ReservationDetails.of(
                reservation,
                reservation.getSpace(),
                representative,
                participants,
                myMemberType,
                reservationProgress);
    }

    @Transactional
    public void cancelMeetingRoom(String email, Long reservationId) {
        LocalDateTime now = LocalDateTime.now();

        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(RESERVATION_NOT_EXISTS));

        if (reservation.getReservationEndDateTime().isBefore(now)) {
            throw new ReservationException(ALREADY_ENDED_RESERVATION);
        }

        List<MemberReservation> memberReservations = reservation.getMemberReservations();
        MemberReservation myMemberReservation = null;

        for (MemberReservation memberReservation : memberReservations) {
            if (memberReservation.getMember().getMemberId() == member.getMemberId()) {
                myMemberReservation = memberReservation;
                break;
            }
        }

        if (myMemberReservation == null) {
            throw new ReservationException(NOT_RESERVED_BY_MEMBER);
        } else if (myMemberReservation.getMemberReservationStatus().equals(CANCELED)) {
            throw new ReservationException(ALREADY_CANCELED_RESERVATION);
        } else if (myMemberReservation.getMemberReservationType().equals(MemberReservationType.REPRESENTATIVE)) {
            memberReservations.forEach(MemberReservation::cancelReservation);
        } else if (myMemberReservation.getMemberReservationType().equals(MemberReservationType.PARTICIPANT)) {
            myMemberReservation.cancelReservation();
        }
    }

    @Transactional
    public void reserveRechargingRoom(String email, ReservationRequestDto.RechargingRoomDto rechargingRoomDto) {
        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        final RechargingRoom rechargingRoom = rechargingRoomRepository.findById(rechargingRoomDto.rechargingRoomId())
                .orElseThrow(() -> new SpaceException(MEETING_ROOM_NOT_FOUND));

        //리차징룸 예약 중복 검증
        if (verifyOverlappingRechargingRoom(member, rechargingRoomDto.startAt())) {
            throw new ReservationException(OVERLAPPING_RECHARGING_ROOM_EXISTS);
        }

        Reservation reservation = rechargingRoomDto.toReservationEntity(rechargingRoom, rechargingRoomDto.startAt(), member);

        reservationRepository.save(reservation);

        publisher.publishEvent(createReserveRechargingRoomEvent(reservation, rechargingRoom, member));
    }

    private boolean verifyOverlappingRechargingRoom(Member member, LocalDateTime startAt) {
        if (reservationRepository.existsOverlappingRechargingRoomReservationByStartAt(member, startAt)) {
            return true;
        }
        return false;
    }

    @Transactional
    public void cancelRechargingRoom(String email, Long reservationId) {
        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(RESERVATION_NOT_EXISTS));

        if(reservation.getMemberReservations().get(0).getMember().getMemberId() != member.getMemberId()) {
            throw new ReservationException(NOT_RESERVED_BY_MEMBER);
        } else if (reservation.getReservationEndDateTime().isBefore(LocalDateTime.now())) {
            throw new ReservationException(ALREADY_ENDED_RESERVATION);
        } else {
            reservation.getMemberReservations().forEach(MemberReservation::cancelReservation);
        }
    }

    public ReservationResponseDto.CheckRechargingRoomOverlap checkRechargingRoomOverlap(String email, LocalDateTime startAt) {

        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));


        // 해당 회원이 해당 시간에 예약한 미팅룸이 있는지 확인
        if (verifyOverlappingMeetingRoom(member, startAt)) {
            return new ReservationResponseDto.CheckRechargingRoomOverlap(ToastType.OVERLAPPING_MEETING_ROOM_EXISTS);
        } else if (verifyOverlappingRechargingRoom(member, startAt)) {
            return new ReservationResponseDto.CheckRechargingRoomOverlap(ToastType.OVERLAPPING_RECHARGING_ROOM_EXISTS);
        }
        return null;
    }

    private boolean verifyOverlappingMeetingRoom(Member member, LocalDateTime startAt) {
        if (reservationRepository.existsOverlappingMeetingRoomReservationsByStartAt(member, startAt)) {
            return true;
        }
        return false;
    }

    private ReserveMeetingRoomEvent createReserveMeetingRoomEvent(Reservation reservation, MeetingRoom meetingRoom, List<Member> participants, List<CancelRechargingRoomNotification> cancelRechargingRoomNotifications) {
        Long reservationId = reservation.getReservationId();
        LocalDateTime reservationDate = reservation.getReservationStartDateTime();
        log.info("Create Reserve Meeting Room Event For Publication. " +
                "Reservation ID: [{}], Reservation Date: [{}]", reservationId, reservationDate);

        String branchName = meetingRoom.getBranch().getBranchName();
        String spaceName = meetingRoom.getSpaceName();
        log.info("Branch Name: [{}], Space Name: [{}]", branchName, spaceName);

        String invitationContent = createMeetingRoomInvitationContent(reservationDate);
        String reservationContent = createMeetingRoomReservationContent(reservationDate, branchName, spaceName);
        log.info("Invitation Content: [{}], Reservation Content: [{}]", invitationContent, reservationContent);

        return new ReserveMeetingRoomEvent(reservationId, reservationDate, invitationContent, reservationContent, participants, cancelRechargingRoomNotifications);
    }

    private ReserveRechargingRoomEvent createReserveRechargingRoomEvent(Reservation reservation, RechargingRoom rechargingRoom, Member member) {
        Long reservationId = reservation.getReservationId();
        LocalDateTime reservationDate = reservation.getReservationStartDateTime();
        log.info("Create Reserve Recharging Room Event For Publication. " +
                "Reservation ID: [{}], Reservation Date: [{}]", reservationId, reservationDate);

        String branchName = rechargingRoom.getBranch().getBranchName();
        String spaceName = rechargingRoom.getSpaceName();
        String reservationContent = createRechargingRoomReservationContent(reservationDate, branchName, spaceName);
        log.info("Branch Name: [{}], Reservation Content: [{}]", branchName, reservationContent);

        return new ReserveRechargingRoomEvent(reservationId, reservationDate, reservationContent, member);
    }

    @Transactional(propagation = REQUIRED)
    @Async
    public void findMeetingRoomEntryNotificationMembers(Long reservationId, String content) {
        log.info("Start Finding Members To Send Meeting Room Entry Notification.");
        Reservation reservation = findReservationWithMemberReservationsAndMembers(reservationId);
        List<Member> members = getAcceptedMembers(reservation);
        if (members.isEmpty()) {
            log.info("Accepted Members Not Found For Reservation. Cancel The Send Notification Task.");
            return;
        }
        log.info("Start Creating And Publishing Meeting Room Entry Notification Event.");
        publisher.publishEvent(new FindMeetingRoomEntryNotificationMembersEvent(reservationId, content, members));
    }

    @Transactional(propagation = REQUIRED)
    @Async
    public void findRechargingRoomEntryNotificationMember(Long reservationId, String content, Member member) {
        log.info("Start Finding Members To Send Recharging Room Entry Notification.");
        Reservation reservation = findReservationWithMemberReservations(reservationId);
        if (!isReservationAccepted(reservation)) {
            log.info("Reservation is Not Accepted. Cancel The Send Notification Task.");
            return;
        }
        log.info("Start Creating And Publishing Recharging Room Entry Notification Event.");
        publisher.publishEvent(new FindRechargingRoomEntryNotificationMemberEvent(reservationId, content, member.getMemberEmail(), member));
    }

    private Reservation findReservationWithMemberReservationsAndMembers(Long reservationId) {
        log.info("Finding Reservation With Member Reservations And Members. Reservation ID: [{}]", reservationId);
        return reservationRepository.findWithMemberReservationsAndMembersById(reservationId)
                .orElseThrow(() -> new ReservationException(RESERVATION_NOT_EXISTS));
    }

    private Reservation findReservationWithMemberReservations(Long reservationId) {
        log.info("Finding Reservation With Member Reservations. Reservation ID: [{}]", reservationId);
        return reservationRepository.findWithMemberReservationsById(reservationId)
                .orElseThrow(() -> new ReservationException(RESERVATION_NOT_EXISTS));
    }

    private List<Member> getAcceptedMembers(Reservation reservation) {
        log.info("Finding Members With Reservation Status Accepted. Reservation ID: [{}]", reservation.getReservationId());
        return reservation.getMemberReservations().stream()
                .filter(memberReservation -> memberReservation.getMemberReservationStatus().equals(ACCEPTED))
                .map(MemberReservation::getMember)
                .toList();
    }

    private boolean isReservationAccepted(Reservation reservation) {
        log.info("Check if Reservation Status is Accepted. Reservation ID: [{}]", reservation.getReservationId());
        return reservation.getMemberReservations().get(0).getMemberReservationStatus().equals(ACCEPTED);
    }
}
