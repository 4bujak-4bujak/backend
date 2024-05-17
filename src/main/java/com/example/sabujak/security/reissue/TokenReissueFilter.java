package com.example.sabujak.security.reissue;

import com.example.sabujak.common.exception.ErrorCode;
import com.example.sabujak.security.exception.CustomAuthenticationException;
import com.example.sabujak.security.jwt.JwtTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.sabujak.security.constants.SecurityConstants.*;
import static com.example.sabujak.security.jwt.constants.JwtConstants.BEARER_TYPE_PREFIX;
import static com.example.sabujak.security.jwt.exception.TokenErrorCode.*;


@RequiredArgsConstructor
public class TokenReissueFilter extends OncePerRequestFilter {

    private static final AntPathRequestMatcher REISSUE_REQUSET_MATCHER
            = new AntPathRequestMatcher(REISSUE_URL_PATTERN, REISSUE_HTTP_METHOD);
    private final ObjectMapper objectMapper;
    private final JwtTokenService tokenService;
    private final TokenReissueSuccessHandler successHandler;
    private final TokenReissueFailureHandler failureHandler;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!isReissueRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = getRefreshToken(request);
        String accessToken = getAccessToken(request);
        if (refreshToken == null) {
            unsuccessfulAuthentication(request, response, REFRESH_TOKEN_NOT_EXIST);
        }

        if (accessToken == null) {
            unsuccessfulAuthentication(request, response, ACCESS_TOKEN_NOT_EXIST);
        }

        try {
            String email = tokenService.validateRefreshTokenAndReturnEmail(accessToken, refreshToken);
            request.setAttribute(VALIDATED_EMAIL, email);
            successfulAuthentication(request, response);
        } catch (ExpiredJwtException e) {
            unsuccessfulAuthentication(request, response, EXPIRED_TOKEN);
        } catch (NullPointerException | SignatureException | MalformedJwtException | UnsupportedJwtException |
                 IllegalArgumentException e) {
            unsuccessfulAuthentication(request, response, INVALID_TOKEN);
        }
    }

    private boolean isReissueRequest(HttpServletRequest request) {
        return REISSUE_REQUSET_MATCHER.matches(request);
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        return refreshToken;
    }

    private String getAccessToken(HttpServletRequest request) {
        String accessTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (accessTokenHeader != null && accessTokenHeader.startsWith(BEARER_TYPE_PREFIX)) {
            return accessTokenHeader.substring(BEARER_TYPE_PREFIX.length());
        }
        return null;
    }

    private void successfulAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        successHandler.onAuthenticationSuccess(request, response, null);
    }

    private void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            ErrorCode errorCode) throws IOException {
        String errorMessage = errorCode.getMessage();
        String customErrorCode = errorCode.getCustomCode();
        AuthenticationException exception = new CustomAuthenticationException(errorMessage, customErrorCode);
        failureHandler.onAuthenticationFailure(request, response, exception);
    }
}
