package com.example.sabujak.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record VerifyRequestDto() {

    @Schema(description = "이메일 인증 코드 Request DTO")
    public record Email(
            @Schema(description = "이메일", example = "test@gmail.com")
            @NotNull String emailAddress) {
    }

    @Schema(description = "이메일 인증 코드 Request DTO")
    public record Phone(@Schema(description = "핸드폰 번호", example = "01012341234")
                        @NotNull String phoneNumber) {
    }

    @Schema(description = "이메일 인증 코드 검증 Request DTO")
    public record EmailCode(@Schema(description = "이메일", example = "test@gmail.com")
                            @NotNull String emailAddress,
                            @Schema(description = "인증 코드", example = "123456")
                            @NotNull
                            String code) {
    }
}
