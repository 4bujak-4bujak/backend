package com.example.sabujak.security.authentication;

import com.example.sabujak.security.jwt.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.sabujak.security.constants.SecurityConstants.EXCEPTION_ATTRIBUTE;
import static com.example.sabujak.security.jwt.constants.JwtConstants.BEARER_TYPE_PREFIX;
import static com.example.sabujak.security.jwt.exception.TokenErrorCode.*;


@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getAccessToken(request);

        if (accessToken == null) {
            request.setAttribute(EXCEPTION_ATTRIBUTE, ACCESS_TOKEN_NOT_EXIST);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

            AbstractAuthenticationToken authenticationToken = jwtTokenService.getAuthentication(accessToken);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);
        } catch (ExpiredJwtException e) {
            request.setAttribute(EXCEPTION_ATTRIBUTE, EXPIRED_TOKEN);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            request.setAttribute(EXCEPTION_ATTRIBUTE, INVALID_TOKEN);
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(HttpServletRequest request) {
        String accessTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (accessTokenHeader != null && accessTokenHeader.startsWith(BEARER_TYPE_PREFIX)) {
            return accessTokenHeader.substring(BEARER_TYPE_PREFIX.length());
        }
        return null;
    }
}
