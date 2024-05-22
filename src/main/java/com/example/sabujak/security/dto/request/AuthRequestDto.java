package com.example.sabujak.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class AuthRequestDto {
    @Schema(description = "로그인 Request DTO")
    public record Login(
            @Schema(description = "이메일", example = "test@gmail.com")
            @NotNull
            @Email
            String email,
            @Schema(description = "비밀번호", example = "!password11")
            @NotNull
            String password
    ) {
    }

    @Schema(description = "비밀번호 재설정 Request DTO")
    public record ResetPassword(
            @Schema(description = "이메일", example = "test@gmail.com")
            @NotNull
            @Email
            String email,
            @Schema(description = "비밀번호", example = "!password11")
            @NotNull
            String password
    ) {
    }

    @Getter
    public static class Access {
        private String email;
        private List<String> authorities = new ArrayList<>();

        private Access(String email, List<String> authorities) {
            this.email = email;
            this.authorities.addAll(authorities);
        }

        public static Access from(String email, List<String> authorities) {
            return new Access(email, authorities);
        }
    }
}

