package com.example.sabujak.member.controller;


import com.example.sabujak.common.response.Response;
import com.example.sabujak.member.dto.response.MemberResponseDto;
import com.example.sabujak.member.service.MemberService;
import com.example.sabujak.security.dto.request.AuthRequestDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "members", description = "회원 API")
//swagger
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가입 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "가입 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "회원 정보 조회", description = "자신의 회원 정보를 조회")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    //swagger
    @GetMapping("/members")
    public ResponseEntity<Response<MemberResponseDto>> getMyInfo(@AuthenticationPrincipal AuthRequestDto.Access access) {

        return ResponseEntity.ok(Response.success(memberService.getMyInfo(access.getEmail())));
    }
}
