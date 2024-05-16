package com.example.sabujak.security.login;


import com.example.sabujak.common.exception.CommonException;
import com.example.sabujak.security.dto.request.AuthRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

import static com.example.sabujak.common.exception.CommonErrorCode.INVALID_REQUEST;
import static com.example.sabujak.security.constants.SecurityConstants.LOGIN_HTTP_METHOD;
import static com.example.sabujak.security.constants.SecurityConstants.LOGIN_URL_PATTERN;

public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER
            = new AntPathRequestMatcher(LOGIN_URL_PATTERN, LOGIN_HTTP_METHOD);

    private final ObjectMapper objectMapper;

    public CustomAuthenticationFilter(ObjectMapper objectMapper) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            AuthRequestDto.Login login = objectMapper.readValue(request.getReader(), AuthRequestDto.Login.class);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(login.email(), login.password());
            return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
        } catch (IOException e) {
            throw new CommonException(INVALID_REQUEST);
        }
    }
}
