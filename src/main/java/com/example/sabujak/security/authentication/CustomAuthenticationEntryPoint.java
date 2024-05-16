package com.example.sabujak.security.authentication;

import com.example.sabujak.common.exception.ErrorCode;
import com.example.sabujak.common.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.sabujak.security.constants.SecurityConstants.EXCEPTION_ATTRIBUTE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ErrorCode errorCode = (ErrorCode) request.getAttribute(EXCEPTION_ATTRIBUTE);
        String errorMessage = (errorCode != null) ? errorCode.getMessage() : authException.getMessage();
        String customErrorCode = (errorCode != null) ? errorCode.getCustomCode() : null;
        sendResponse(response, errorMessage, customErrorCode);
    }

    private void sendResponse(HttpServletResponse response, String errorMessage, String customErrorCode) throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.setStatus(UNAUTHORIZED.value());
        objectMapper.writeValue(
                response.getWriter(),
                Response.fail(customErrorCode, errorMessage)
        );
    }
}
