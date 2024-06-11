package com.example.sabujak.member.controller;


import com.example.sabujak.common.response.Response;
import com.example.sabujak.member.dto.request.MemberRequestDto;
import com.example.sabujak.member.dto.response.MemberModalIgnoredResponseDto;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Tag(name = "members", description = "회원 API")
//swagger
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "회원 정보 조회", description = "자신의 회원 정보를 조회")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })

    @GetMapping("/members")
    public ResponseEntity<Response<MemberResponseDto.AllInformation>> getMyInfo(@AuthenticationPrincipal AuthRequestDto.Access access) {

        return ResponseEntity.ok(Response.success(memberService.getMyInfo(access.getEmail())));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검증 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "검증 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "비밀번호 검증", description = "비밀번호 변경을 위한 기존 비밀번호 검증")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @PostMapping("/members/password/verify")
    public ResponseEntity<Response<Void>> verifyPassword(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                          @RequestBody MemberRequestDto.VerifyPassword verifyPassword) {
        memberService.verifyPassword(access.getEmail(), verifyPassword.password());
        return ResponseEntity.ok(Response.success(null));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "변경 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "비밀번호 변경", description = "자신의 비밀 번호를 변경")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @PatchMapping("/members/password")
    public ResponseEntity<Response<Void>> changMyPassword(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                          @RequestBody MemberRequestDto.ChangePassword changePassword) {
        memberService.changeMyPassword(access.getEmail(), changePassword.newPassword());
        return ResponseEntity.ok(Response.success(null));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "탈퇴 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "탈퇴 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @DeleteMapping("/members")
    public ResponseEntity<Response<Void>> signOut(@AuthenticationPrincipal AuthRequestDto.Access access) {
        memberService.signOut(access.getEmail());
        return ResponseEntity.ok(Response.success(null));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "변경 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "회원 이미지 변경", description = "회원의 프로필 이미지 변경")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @PatchMapping(value = "/members/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<Void>> changeMemberImage(@AuthenticationPrincipal AuthRequestDto.Access access,
                                                            @RequestPart MultipartFile image) {
        memberService.changeMemberImage(access.getEmail(), image);
        return ResponseEntity.ok(Response.success(null));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "회원 모달 다시보지 않기 체크 여부 조회", description = "회원이 24시간 내에 다시보지않기를 체크했는지 여부를 조회한다.")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @GetMapping("/members/modal")
    public ResponseEntity<Response<MemberModalIgnoredResponseDto>> checkMemberModalIgnored( @AuthenticationPrincipal AuthRequestDto.Access access
    )
    {
        return ResponseEntity.ok(Response.success(memberService.checkModalIgnored(access.getEmail())));
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "업데이트 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "회원 모달 다시보지 않기 체크 여부 업데이트", description = "회원이 24시간 내에 다시보지않기를 체크했는지 여부를 업데이트. ignored = false -> 다시보지않기 누른 적 없거나 24시간이 지남 따라서 모달 띄워야함")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    @PatchMapping("/members/modal")
    public ResponseEntity<Response<Void>> changeMemberModalIgnored( @AuthenticationPrincipal AuthRequestDto.Access access){
        memberService.changeModalIgnored(access.getEmail());
        return ResponseEntity.ok(Response.success(null));
    }

}
