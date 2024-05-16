package com.example.sabujak.security.jwt.constants;

public class JwtConstants {

    public static final String BEARER_TYPE_PREFIX = "Bearer ";
    public static final String JWT_AUTHORITIES_KEY = "authorities";
    public static final String JWT_USER_KEY = "email";
    public static final long JWT_ACCESS_TOKEN_EXPIRE_TIME = 5 * 60 * 1000L;     // 5분
    public static final long JWT_REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일

    public static final String REFRESH_TOKEN_PREFIX = "RFT:";
}
