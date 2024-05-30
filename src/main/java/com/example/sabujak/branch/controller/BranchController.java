package com.example.sabujak.branch.controller;

import com.example.sabujak.branch.dto.response.BranchResponseDto;
import com.example.sabujak.branch.service.BranchService;
import com.example.sabujak.common.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}
