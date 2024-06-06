package com.example.sabujak.space.controller;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.security.dto.request.AuthRequestDto;
import com.example.sabujak.space.dto.response.FocusDeskResponseDto;
import com.example.sabujak.space.dto.response.MeetingRoomResponseDto;
import com.example.sabujak.space.entity.MeetingRoomType;
import com.example.sabujak.space.service.SpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "spaces", description = "예약 공간 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/spaces")
public class SpaceController {

    private final SpaceService spaceService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "미팅룸 리스트 조회", description = "지점, 날짜, 비품 필터, 정렬기준에 따른 미팅룸 리스트 조회")
    @Parameters({
            @Parameter(name = "access", hidden = true),
            @Parameter(name = "startAt", description = "시작 시간", example = "2024-05-28T09:00:00"),
            @Parameter(name = "endAt", description = "종료 시간", example = "2024-05-28T18:00:00"),
            @Parameter(name = "branchName", description = "지점명", example = "구로점"),
            @Parameter(name = "meetingRoomTypes", description = "미팅룸 타입", example = "SMALL"),
            @Parameter(name = "projectorExists", description = "프로젝터 여부", example = "false"),
            @Parameter(name = "canVideoConference", description = "화상장비 여부", example = "false"),
            @Parameter(name = "isPrivate", description = "프라이빗 여부", example = "false"),
            @Parameter(name = "sortTarget", description = "정렬 기준 (roomCapacity (인원수), roomFloor (층수))", example = "roomCapacity"),
            @Parameter(name = "sortDirection", description = "정렬 방식 (ASC, DESC)", example = "ASC"),
    })
    @GetMapping("/meeting-rooms")
    public ResponseEntity<Response<MeetingRoomResponseDto.MeetingRoomList>> getMeetingRoomList(
            @AuthenticationPrincipal AuthRequestDto.Access access,
            @RequestParam LocalDateTime startAt,
            @RequestParam LocalDateTime endAt,
            @RequestParam String branchName,
            @RequestParam List<MeetingRoomType> meetingRoomTypes,
            @RequestParam boolean projectorExists,
            @RequestParam boolean canVideoConference,
            @RequestParam boolean isPrivate,
            @RequestParam(required = false, defaultValue = "roomCapacity") String sortTarget,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        return ResponseEntity.ok(Response.success(spaceService.getMeetingRoomList(access.getEmail(), startAt, endAt,
                branchName, meetingRoomTypes, projectorExists, canVideoConference, isPrivate, sortTarget, sortDirection)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "미팅룸 상세 조회", description = "예약 창 들어오고 예약할 때 미팅룸 상세 조회")
    @Parameters({
            @Parameter(name = "meetingRoomId", description = "미팅룸 Id", example = "1")
    })
    @GetMapping("/meeting-rooms/{meetingRoomId}")
    public ResponseEntity<Response<MeetingRoomResponseDto.MeetingRoomDetails>> getMeetingRoomDetails(@PathVariable Long meetingRoomId) {
        return ResponseEntity.ok(Response.success(spaceService.getMeetingRoomDetails(meetingRoomId)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "포커스존 현재 이용 가능 좌석 수 조회", description = "해당 지점의 포커스 존 내의 현재 이용 가능 좌석 수 조회")
    @Parameters({
            @Parameter(name = "branchId", description = "지점 Id", example = "1")
    })
    @GetMapping("/focus-zone/{branchId}/available-seat-count")
    public ResponseEntity<Response<FocusDeskResponseDto.AvailableSeatCountInformation>> getAvailableSeatCount(@PathVariable Long branchId) {
        return ResponseEntity.ok(Response.success(spaceService.getAvailableSeatCount(branchId)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "포커스존 좌석 정보 리스트", description = "해당 지점의 포커스 존 좌석 정보(예약 가능한지) 리스트")
    @Parameters({
            @Parameter(name = "branchId", description = "지점 Id", example = "1")
    })
    @GetMapping("/focus-zone/{branchId}")
    public ResponseEntity<Response<List<FocusDeskResponseDto.FocusDeskForList>>> getFocusDeskList(@PathVariable Long branchId) {
        return ResponseEntity.ok(Response.success(spaceService.getFocusDeskList(branchId)));
    }
}
