package com.example.sabujak.security.constants;

public class SecurityConstants {
    public static final int EMAIL_CODE_EXPIRATION_MILLIS = 300_000; //이메일 인증 코드 유효 5분
    public static final int PHONE_CODE_EXPIRATION_MILLIS = 300_000; //핸드폰 인증 코드 유효 5분

    public static final String EMAIL_CODE_PREFIX = "email_code:";
    public static final String PHONE_CODE_PREFIX = "phone_code:";

    public static final String[] AUTH_WHITELIST = {
            "/login", "/reissue", "/auth/**",
            "/", "/error",

            "/favicon.ico",

            /* swagger v3 */
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/api-docs",
    };
}
