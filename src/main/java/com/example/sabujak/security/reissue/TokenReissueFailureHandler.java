package com.example.sabujak.security.reissue;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.security.exception.CustomAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Component
public class TokenReissueFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String errorMessage = exception.getMessage();
        String customErrorCode = null;
        if(exception instanceof CustomAuthenticationException) {
            customErrorCode = ((CustomAuthenticationException) exception).getCustomErrorCode();
        }
        sendFailResponse(response, errorMessage, customErrorCode);
    }

    private void sendFailResponse(HttpServletResponse response, String errorMessage, String customErrorCode)
            throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.setStatus(UNAUTHORIZED.value());
        objectMapper.writeValue(
                response.getWriter(),
                Response.fail(customErrorCode, errorMessage)
        );
    }
}
