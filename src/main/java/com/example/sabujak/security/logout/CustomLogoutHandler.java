package com.example.sabujak.security.logout;

import com.example.sabujak.security.jwt.JwtTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import static com.example.sabujak.security.jwt.constants.JwtConstants.BEARER_TYPE_PREFIX;


@RequiredArgsConstructor
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenService tokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Cookie[] cookies = request.getCookies();

        deleteRefreshTokenCookie(request, response);
        String accessToken = getAccessToken(request);

        tokenService.removeRefreshToken(accessToken);
        //refresh 토큰도 같이 넘겨서 redis랑 비교후 삭제 하려 했으나
        //refresh 토큰이 어쨋든 access 관련 redis에 있는 refresh 토큰을 삭제해야 하므로 access만 넘기기로
    }

    private void deleteRefreshTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }
    }

    private String getAccessToken (HttpServletRequest request) {
        String accessTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (accessTokenHeader != null && accessTokenHeader.startsWith(BEARER_TYPE_PREFIX)) {
            return accessTokenHeader.substring(BEARER_TYPE_PREFIX.length());
        }
        return null;
    }
}
