package com.example.sabujak.branch.controller;

import com.example.sabujak.branch.dto.response.BranchDistanceResponseDto;
import com.example.sabujak.branch.dto.response.BranchResponseDto;
import com.example.sabujak.branch.dto.response.BranchWithSpaceDto;
import com.example.sabujak.branch.service.BranchService;
import com.example.sabujak.common.response.Response;
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

@Tag(name = "branches", description = "지점 API")
@RequiredArgsConstructor
@RequestMapping("/branches")
@RestController
public class BranchController {

    private final BranchService branchService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "지점 단건 조회", description = "지점의 이름을 통해 해당 지점 조회")
    @GetMapping("/{name}")
    public ResponseEntity<Response<BranchResponseDto>> getBranchByName(@PathVariable String name) {
        return ResponseEntity.ok(Response.success(branchService.findByBranchName(name)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "전체 지점 조회", description = "전체 지점 조회")
    @GetMapping
    public ResponseEntity<Response<List<BranchResponseDto>>> getAllBranches() {
        return ResponseEntity.ok(Response.success(branchService.findAll()));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "지점 단건 조회", description = "지점의 이름과 현재 시간을 통해 지점 정보와 사용 가능한 회의실 개수 반환")
    @Parameters({
            @Parameter(name = "name", description = "지점명", example = "구로점"),
            @Parameter(name = "now", description = "현재 시간", example = "2024-06-03T09:00:00")
    })
    @GetMapping("/{name}/space")
    public ResponseEntity<Response<BranchWithSpaceDto>> getBranchWithSpace(@PathVariable String name,
                                                                           @RequestParam LocalDateTime now) {
        return ResponseEntity.ok(Response.success(branchService.getBranchWithSpace(now, name)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "주변 지점 조회", description = "현재 위치 기준 가까운 위치 지점 최대 2개 반환")
    @Parameters({
            @Parameter(name = "latitude", description = "사용자 위도"),
            @Parameter(name = "longitude", description = "사용자 경도")
    })
    @GetMapping("/distance")
    public ResponseEntity<Response<List<BranchDistanceResponseDto>>> getNearBranchesByPosition(
            @RequestParam double latitude,
            @RequestParam double longitude
    ){
        return ResponseEntity.ok(Response.success(branchService.getNearBranchByPosition(latitude, longitude)));
    }


}
