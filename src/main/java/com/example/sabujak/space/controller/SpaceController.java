package com.example.sabujak.space.controller;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.space.dto.response.SpaceResponseDto;
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
    public ResponseEntity<Response<List<SpaceResponseDto.MeetingRoomDto>>> getMeetingRoomList(
            @RequestParam LocalDateTime startAt,
            @RequestParam LocalDateTime endAt,
            @RequestParam String branchName,
            @RequestParam List<MeetingRoomType> meetingRoomTypes,
            @RequestParam boolean projectorExists,
            @RequestParam boolean canVideoConference,
            @RequestParam boolean isPrivate,
            @RequestParam(required = false, defaultValue = "roomCapacity") String sortTarget,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        return ResponseEntity.ok(Response.success(spaceService.getMeetingRoomList(startAt, endAt,
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
    public ResponseEntity<Response<SpaceResponseDto.MeetingRoomDetails>> getMeetingRoomDetails(@PathVariable Long meetingRoomId) {
        return ResponseEntity.ok(Response.success(spaceService.getMeetingRoomDetails(meetingRoomId)));
    }
}
