package com.example.sabujak.security.controller;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.member.dto.request.MemberRequestDto;
import com.example.sabujak.security.dto.request.VerifyRequestDto;
import com.example.sabujak.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가입 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "가입 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "회원 가입", description = "회원 가입 시도")
    @PostMapping("/members")
    public ResponseEntity<Response<Void>> signUp(@RequestBody @Valid MemberRequestDto.SignUp signUp) {
        authService.signUp(signUp);

        return ResponseEntity.ok(Response.success(null));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 요청 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "인증 요청 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "이메일 인증 요청", description = "입력한 이메일로 인증 코드를 보냅니다.")
    @PostMapping("/auth/email")
    public ResponseEntity<Response<Void>> requestEmailVerify(@RequestBody @Valid VerifyRequestDto.Email email) throws MessagingException {
        authService.requestEmailVerify(email);

        return ResponseEntity.ok(Response.success(null));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "코드 검증 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "코드 검증 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "이메일 코드 검증", description = "사용자가 입력한 코드와 이메일로 발송한 코드가 일치하는지 확인합니다.")
    @PostMapping("/auth/email/verify")
    public ResponseEntity<Response<Void>> verifyEmailCode(@RequestBody @Valid VerifyRequestDto.EmailCode emailCode) {
        boolean isSuccess = authService.verifyEmailCode(emailCode);
        if (isSuccess) {
            return ResponseEntity.ok(Response.success(null));
        } else {
            return ResponseEntity.ok(Response.fail("코드 검증에 실패했습니다."));
        }
    }
}