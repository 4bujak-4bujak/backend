package com.example.sabujak.security.login;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.security.dto.request.UserDetailsDto;
import com.example.sabujak.security.jwt.JwtTokenService;
import com.example.sabujak.security.jwt.dto.TokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UserDetailsDto userDetailsDto = (UserDetailsDto) authentication.getPrincipal();

        TokenDto.AccessAndRefreshToken tokenDto = tokenService.generateTokenAfterLoginSuccess(userDetailsDto);

        sendSuccessResponse(response, tokenDto);
    }


    private void sendSuccessResponse(HttpServletResponse response, TokenDto.AccessAndRefreshToken tokenDto)
            throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.setStatus(HttpServletResponse.SC_OK);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenDto.refreshToken())
                .httpOnly(true)
                .maxAge(tokenService.getExpiration(tokenDto.refreshToken()).intValue())
                .secure(true)
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());


        objectMapper.writeValue(
                response.getWriter(),
                Response.success(new TokenDto.AccessToken(tokenDto.accessToken()))
        );
    }
}
