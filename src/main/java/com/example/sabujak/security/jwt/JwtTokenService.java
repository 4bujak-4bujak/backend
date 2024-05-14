package com.example.sabujak.security.jwt;

import com.example.sabujak.common.redis.service.RedisService;
import com.example.sabujak.security.dto.request.AuthRequestDto;
import com.example.sabujak.security.dto.request.UserDetailsDto;
import com.example.sabujak.security.jwt.dto.TokenDto;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.sabujak.security.jwt.constants.JwtConstants.*;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final RedisService redisService;
    private final JwtTokenUtil tokenUtil;
    private final UserDetailsService userDetailsService;

    public TokenDto.AccessAndRefreshToken generateTokenAfterLoginSuccess(UserDetailsDto userDetailsDto) {

        final String userRole = userDetailsDto.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        final String userName = userDetailsDto.getUsername();
        Instant now = Instant.now();

        String accessToken = tokenUtil.generateAccessToken(now, userName, userRole);
        String refreshToken = tokenUtil.generateRefreshToken(now);

        redisService.set(REFRESH_TOKEN_PREFIX + userName, refreshToken, JWT_REFRESH_TOKEN_EXPIRE_TIME);

        return TokenDto.AccessAndRefreshToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Long getExpiration(String token) {
        return tokenUtil.getExpiration(token);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String accessToken) {
        Claims claims = tokenUtil.parseTokenClaims(accessToken);

        String email = claims.get(JWT_USER_KEY).toString();
        List<String> authorities = Arrays.asList(claims.get(JWT_AUTHORITIES_KEY).toString().split(","));

        AuthRequestDto.Access access = AuthRequestDto.Access.from(email, authorities);

        return new UsernamePasswordAuthenticationToken(
                access,
                "",
                access.getAuthorities().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }

    public String validateRefreshTokenAndReturnEmail(String accessToken, String refreshToken) {
        Claims claims = tokenUtil.parseExpiredAccessTokenClaims(accessToken);
        Claims refreshClaims = tokenUtil.parseClaims(refreshToken);
        String email = claims.get(JWT_USER_KEY).toString();

        String refreshTokenInRedis = redisService.get(REFRESH_TOKEN_PREFIX + email, String.class).get();
        if(refreshTokenInRedis.equals(refreshToken)) {
            return email;
        } return null;
    }

    public TokenDto.AccessToken reissueToken(String email) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final String userRole = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        TokenDto.AccessToken reissuedAccessToken = new TokenDto.AccessToken(tokenUtil.generateAccessToken(Instant.now(), email, userRole));
        return reissuedAccessToken;
    }
}
