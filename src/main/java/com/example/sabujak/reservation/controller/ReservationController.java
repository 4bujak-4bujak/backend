//package com.example.sabujak.reservation.controller;
//
//import com.example.sabujak.common.response.Response;
//import com.example.sabujak.member.dto.response.MemberResponseDto;
//import com.example.sabujak.reservation.service.ReservationService;
//import com.example.sabujak.security.dto.request.AuthRequestDto;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.Parameters;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Tag(name = "reservations", description = "예약 관련 API")
//@RestController
//@RequiredArgsConstructor
//public class ReservationController {
//
//    private final ReservationService reservationService;
//
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
//            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
//    @Operation(summary = "미팅룸 리스트 조회", description = "지점, 날짜, 부품 필터, 정렬기준에 따른 미팅룸 리스트 조회")
//    @Parameters({
//            @Parameter(name = "branch", description = "지점명", example = "구로점"),
//            @Parameter(name = "date", description = "날짜", example = "2024-05-22"),
//    })
//
//    @GetMapping("/members")
//    public ResponseEntity<Response<MemberResponseDto.AllInformation>> getMyInfo(@AuthenticationPrincipal AuthRequestDto.Access access) {
//
//        return ResponseEntity.ok(Response.success(memberService.getMyInfo(access.getEmail())));
//    }
//}
