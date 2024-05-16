package com.example.sabujak.security.reissue;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.security.constants.SecurityConstants;
import com.example.sabujak.security.jwt.JwtTokenService;
import com.example.sabujak.security.jwt.dto.TokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class TokenReissueSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String email = (String) request.getAttribute(SecurityConstants.VALIDATED_EMAIL);
        TokenDto.AccessToken reissuedAccessToken = tokenService.reissueToken(email);
        sendSuccessResponse(response, reissuedAccessToken);
    }

    private void sendSuccessResponse(HttpServletResponse response, TokenDto.AccessToken reissuedAccesToken)
            throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.setStatus(HttpStatus.OK.value());
        objectMapper.writeValue(
                response.getWriter(),
                Response.success(reissuedAccesToken)
        );
    }
}
