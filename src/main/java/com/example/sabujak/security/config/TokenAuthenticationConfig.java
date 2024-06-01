package com.example.sabujak.security.config;

import com.example.sabujak.security.authentication.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@RequiredArgsConstructor
public class TokenAuthenticationConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Override
    public void configure(HttpSecurity builder) {
        builder.addFilterBefore(
                tokenAuthenticationFilter,
                AuthorizationFilter.class
        );
    }
}
