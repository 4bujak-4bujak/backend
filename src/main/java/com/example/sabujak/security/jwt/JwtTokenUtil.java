package com.example.sabujak.security.jwt;

import com.example.sabujak.security.jwt.exception.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static com.example.sabujak.security.jwt.constants.JwtConstants.*;
import static com.example.sabujak.security.jwt.exception.TokenErrorCode.EXPIRED_TOKEN;
import static com.example.sabujak.security.jwt.exception.TokenErrorCode.INVALID_TOKEN;

@Component
public class JwtTokenUtil {

    private final JwtProperties jwtProperties;
    private final Key key;

    public JwtTokenUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Instant now, String email, String authorities) {

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(Date.from(now))
                .claim(JWT_USER_KEY, email)
                .claim(JWT_AUTHORITIES_KEY, authorities)
                .setExpiration(Date.from(now.plus(JWT_ACCESS_TOKEN_EXPIRE_TIME, ChronoUnit.MILLIS)))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(Instant now) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(JWT_REFRESH_TOKEN_EXPIRE_TIME, ChronoUnit.MILLIS)))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenException(EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new TokenException(INVALID_TOKEN);
        }
    }

    public Long getExpiration(String token) {

        Claims claims = parseClaims(token);

        Date expiration = claims.getExpiration();
        Long now = Date.from(Instant.now()).getTime();
        return (expiration.getTime() - now);
    }

    public Claims parseTokenClaims(String accessToken) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    }
}
