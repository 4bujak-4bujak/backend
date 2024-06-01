package com.example.sabujak.security.config;

import com.example.sabujak.security.jwt.JwtTokenService;
import com.example.sabujak.security.reissue.TokenReissueFailureHandler;
import com.example.sabujak.security.reissue.TokenReissueFilter;
import com.example.sabujak.security.reissue.TokenReissueSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@RequiredArgsConstructor
public class TokenReissueConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final ObjectMapper objectMapper;
    private final TokenReissueSuccessHandler tokenReissueSuccessHandler;
    private final TokenReissueFailureHandler tokenReissueFailureHandler;
    private final JwtTokenService jwtTokenService;

    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(
                tokenReissueFilter(),
                UsernamePasswordAuthenticationFilter.class
        );
    }

    public TokenReissueFilter tokenReissueFilter() {
        return new TokenReissueFilter(
                objectMapper,
                jwtTokenService,
                tokenReissueSuccessHandler,
                tokenReissueFailureHandler
        );
    }
}
