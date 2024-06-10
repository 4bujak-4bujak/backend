package com.example.sabujak.reservation.controller;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.reservation.dto.request.ReservationRequestDto;
import com.example.sabujak.reservation.dto.response.ReservationHistoryResponse;
import com.example.sabujak.reservation.dto.response.ReservationResponseDto;
import com.example.sabujak.reservation.service.ReservationService;
import com.example.sabujak.security.dto.request.AuthRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "reservations", description = "예약 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "예약 과정중 초대 가능 멤버 조회", description = "예약 과정에서 같은 회사에 다니고 특정 검증을 거쳐 참석자로 초대될 수 있는 사람 조회")
    @Parameters({
            @Parameter(name = "access", hidden = true),
            @Parameter(name = "startAt", description = "시작 시간", example = "2024-05-28T09:00:00"),
            @Parameter(name = "endAt", description = "종료 시간", example = "2024-05-28T18:00:00"),
            @Parameter(name = "searchTerm", description = "검색어에 맞는 이메일, 이름을 찾음", example = "주우민", required = false)
    })

    @GetMapping("/members")
    public ResponseEntity<Response<ReservationResponseDto.FindMemberList>> findMembers(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                                                       @RequestParam LocalDateTime startAt,
                                                                                       @RequestParam LocalDateTime endAt,
                                                                                       @RequestParam(required = false) String searchTerm) {
        return ResponseEntity.ok(Response.success(reservationService.findMembers(access.getEmail(), searchTerm, startAt, endAt)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "당일 예약 내역 리스트 조회", description = "현재시간 기준 이용중이거나 오늘 시작하는 예약 조회")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @GetMapping("/today")
    public ResponseEntity<Response<List<ReservationHistoryResponse.ReservationForList>>> getTodayReservations(@AuthenticationPrincipal AuthRequestDto.Access access) {
        return ResponseEntity.ok(Response.success(reservationService.getTodayReservations(access.getEmail())));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "30일 내 예약 내역 리스트 조회", description = "오늘 제외 최대 30일 이내 모든 예약 내역 리스트 조회")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @GetMapping
    public ResponseEntity<Response<List<ReservationHistoryResponse.ReservationForList>>> getReservations(@AuthenticationPrincipal AuthRequestDto.Access access) {
        return ResponseEntity.ok(Response.success(reservationService.getReservationsFor30Days(access.getEmail())));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "예약 상세 조회", description = "선택한 예약 상세 조회")
    @Parameters({
            @Parameter(name = "access", hidden = true),
            @Parameter(name = "reservationId", description = "예약 Id", example = "1")
    })
    @GetMapping("/{reservationId}")
    public ResponseEntity<Response<ReservationHistoryResponse.ReservationDetails>> getReservationDetails(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                                                                         @PathVariable Long reservationId) {
        return ResponseEntity.ok(Response.success(reservationService.getReservationDetails(access.getEmail(), reservationId)));
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "예약 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "미팅룸 예약", description = "미팅룸 예약 관련 모든 정보들을 받고 최종 예약")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @PostMapping("/meeting-rooms")
    public ResponseEntity<Response<Void>> reserveMeetingRoom(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                             @Valid @RequestBody ReservationRequestDto.MeetingRoomDto meetingRoomDto) {
        reservationService.reserveMeetingRoom(access.getEmail(), meetingRoomDto);
        return ResponseEntity.ok(Response.success(null));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "예약 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "미팅룸 예약 취소", description = "미팅룸 예약 id를 받고 예약 취소 (예약자는 참여자까지 전부 취소 참여자는 자신만)")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @DeleteMapping("/meeting-rooms/{reservationId}")
    public ResponseEntity<Response<Void>> cancelMeetingRoomReservation(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                                       @PathVariable Long reservationId) {
        reservationService.cancelMeetingRoom(access.getEmail(), reservationId);
        return ResponseEntity.ok(Response.success(null));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "예약 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "리차징룸 예약", description = "리차징룸을 30분동안 예약")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @PostMapping("/recharging-rooms")
    public ResponseEntity<Response<Void>> reserveRechargingRoom(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                                @Valid @RequestBody ReservationRequestDto.RechargingRoomDto rechargingRoomDto) {
        reservationService.reserveRechargingRoom(access.getEmail(), rechargingRoomDto);
        return ResponseEntity.ok(Response.success(null));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검증 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "검증 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "리차징룸 예약 중복 검증", description = "선택한 시간에 이미 리차징룸이나 미팅룸을 예약 했는지 검증")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @GetMapping("/recharging-rooms/check-overlap")
    public ResponseEntity<Response<ReservationResponseDto.CheckRechargingRoomOverlap>> checkRechargingRoomOverlap(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                                                                                  @RequestParam LocalDateTime startAt) {
        return ResponseEntity.ok(Response.success(reservationService.checkRechargingRoomOverlap(access.getEmail(), startAt)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "취소 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "취소 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "리차징룸 예약 취소", description = "리차징룸 예약 id를 받고 예약 취소")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @DeleteMapping("/recharging-rooms/{reservationId}")
    public ResponseEntity<Response<Void>> cancelRechargingRoomReservation(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                                          @PathVariable Long reservationId) {
        reservationService.cancelRechargingRoom(access.getEmail(), reservationId);
        return ResponseEntity.ok(Response.success(null));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "예약 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "포커스 데스크 예약", description = "포커스존 예약 관련 모든 정보들을 받고 최종 예약")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @PostMapping("/focus-desks")
    public ResponseEntity<Response<Void>> reserveFocusDesk(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                           @Valid @RequestBody ReservationRequestDto.FocusDeskDto focusDeskDto) {
        reservationService.reserveFocusDesk(access.getEmail(), focusDeskDto);
        return ResponseEntity.ok(Response.success(null));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "예약 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "포커스 데스크 예약 검증", description = "포커스존 예약 전 기존에 사용중이 있는 좌석 있는지 확인")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @GetMapping("/focus-desks/check-overlap/{focusDeskId}")
    public ResponseEntity<Response<ReservationResponseDto.CheckFocusDeskOverlap>> checkFocusDeskOverlap(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                                                                        @PathVariable Long focusDeskId) {
        return ResponseEntity.ok(Response.success(reservationService.checkFocusDeskOverlap(access.getEmail(), focusDeskId)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 종료 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "예약 종료 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "포커스 데스크 예약 종료", description = "포커스존 좌석 ID 받고 해당 예약자인지 검증 후 예약 종료")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @DeleteMapping("/focus-desks/{focusDeskId}")
    public ResponseEntity<Response<Void>> endUseDesk(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                     @PathVariable Long focusDeskId) {
        reservationService.endUseFocusDesk(access.getEmail(), focusDeskId);
        return ResponseEntity.ok(Response.success(null));
    }
}
