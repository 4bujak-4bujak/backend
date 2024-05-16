package com.example.sabujak.security.jwt.dto;

import lombok.Builder;


public class TokenDto {

    @Builder
    public record AccessAndRefreshToken(String accessToken, String refreshToken) {

    }

    public record AccessToken(String accessToken) {

    }
}
