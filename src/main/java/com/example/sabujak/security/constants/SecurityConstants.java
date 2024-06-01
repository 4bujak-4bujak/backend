package com.example.sabujak.security.constants;

public class SecurityConstants {
    public static final int EMAIL_CODE_EXPIRATION_MILLIS = 300_000; //이메일 인증 코드 유효 5분
    public static final int PHONE_CODE_EXPIRATION_MILLIS = 300_000; //핸드폰 인증 코드 유효 5분

    public static final String EMAIL_CODE_PREFIX = "email_code:";
    public static final String PHONE_CODE_PREFIX = "phone_code:";

    public static final String LOGIN_URL_PATTERN = "/login";
    public static final String LOGIN_HTTP_METHOD = "POST";
    public static final String REISSUE_URL_PATTERN = "/reissue";
    public static final String REISSUE_HTTP_METHOD = "POST";


    public static final String EXCEPTION_ATTRIBUTE = "exception";
    public static final String VALIDATED_EMAIL = "validatedEmail";


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

    public static final String[] ALLOWED_HEADERS = {
            "Content-Type", "Authorization", "X-Requested-With", "Accept",
            "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers",
            "Cache-Control", "Referer", "Content-Disposition"
    };

    public static final String[] ALLOWED_ORIGINS = {
            "http://localhost:3000", "http://172.30.1.*", "http://localhost:8080",
            "https://4busak.vercel.app","https://4busak-git-main-eunhaks-projects.vercel.app","https://4busak-hqmkp0gky-eunhaks-projects.vercel.app",
            "https://*.vercel.app", "https://joo-api.store"
    };

    public static final String[] PERMIT_ALL_GET_ENDPOINTS = {
            "/posts",
            "/posts/{postId}",
            "/posts/{postId}/comments"
    };
}
