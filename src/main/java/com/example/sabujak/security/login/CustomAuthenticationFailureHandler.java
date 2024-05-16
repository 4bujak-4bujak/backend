package com.example.sabujak.security.login;


import com.example.sabujak.common.exception.ErrorCode;
import com.example.sabujak.common.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.sabujak.security.exception.AuthErrorCode.INVALID_EMAIL_OR_PASSWORD;
import static com.example.sabujak.security.exception.AuthErrorCode.UNKOWN_ERROR;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        ErrorCode errorCode;
        if (exception instanceof BadCredentialsException || exception instanceof InternalAuthenticationServiceException) {
            errorCode = INVALID_EMAIL_OR_PASSWORD;
        } else {
            errorCode = UNKOWN_ERROR;
        }
        sendFailResponse(response, errorCode);
    }


    private void sendFailResponse(HttpServletResponse response, ErrorCode errorCode)
            throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.setStatus(errorCode.getHttpStatus().value());
        objectMapper.writeValue(
                response.getWriter(),
                Response.fail(errorCode.getCustomCode(), errorCode.getMessage())
        );
    }
}
