package com.example.sabujak.reservation.controller;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.reservation.dto.request.ReservationRequestDto;
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

@Tag(name = "reservations", description = "예약 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
//            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
//    @Operation(summary = "예약 과정중 초대 가능 멤버 조회", description = "예약 과정에서 같은 회사에 다니고 특정 검증을 거쳐 참석자로 초대될 수 있는 사람 조회")
//    @Parameters({
//            @Parameter(name = "access", hidden = true),
//            @Parameter(name = "searchTerm", description = "검색어에 맞는 이메일, 이름을 찾음", example = "주우민", required = false)
//    })
//
//    @GetMapping("/members")
//    public ResponseEntity<Response<List<ReservationResponseDto.FindMember>>> findMembers(@AuthenticationPrincipal AuthRequestDto.Access access,
//                                                                                         @RequestParam(required = false) String searchTerm) {
//
//        return ResponseEntity.ok(Response.success(reservationService.findMembers(access.getEmail(), searchTerm)));
//    }
//
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "예약 성공", content = @Content(schema = @Schema(implementation = Response.class))),
//            @ApiResponse(responseCode = "404", description = "예약 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
//    @Operation(summary = "미팅룸 예약", description = "미팅룸 예약 관련 모든 정보들을 받고 최종 예약")
//    @Parameters({
//            @Parameter(name = "access", hidden = true)
//    })
//    @PostMapping("/meeting-rooms")
//    public ResponseEntity<Response<Void>> reserveMeetingRoom(@AuthenticationPrincipal AuthRequestDto.Access access,
//                                                             @Valid @RequestBody ReservationRequestDto.MeetingRoomDto meetingRoomDto) {
//        reservationService.reserveMeetingRoom(access.getEmail(), meetingRoomDto);
//        return ResponseEntity.ok(Response.success(null));
//    }

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
    public ResponseEntity<Response<ReservationResponseDto.CheckOverlap>> checkOverlap(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                                                      @PathVariable Long focusDeskId) {
        return ResponseEntity.ok(Response.success(reservationService.checkOverlap(access.getEmail(), focusDeskId)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 종료 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "예약 종료 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "포커스 데스크 예약 종료", description = "포커스존 좌석 ID 받고 해당 예약자인지 검증 후 예약 종료")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @DeleteMapping("/focus-desks")
    public ResponseEntity<Response<Void>> endUseDesk(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                     @Valid @RequestBody ReservationRequestDto.FocusDeskDto focusDeskDto) {
        reservationService.endUseFocusDesk(access.getEmail(), focusDeskDto);
        return ResponseEntity.ok(Response.success(null));
    }
}
