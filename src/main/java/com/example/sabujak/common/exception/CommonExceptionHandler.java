package com.example.sabujak.common.exception;

import com.example.sabujak.common.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.sabujak.common.exception.CommonErrorCode.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<Response<String>> handleCustomException(CustomException e) {
        HttpStatus status = e.getErrorCode().getHttpStatus();
        String message = e.getErrorCode().getMessage();
        String customCode = e.getErrorCode().getCustomCode();

        log.error("[CustomException] Status: {}, Message: {}", status, message);

        return new ResponseEntity<>(Response.fail(customCode, message), status);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Response<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        List<String> errorMessages = bindingResult.getFieldErrors().stream()
                .map(fieldError -> "[" + fieldError.getField() + "] " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errorMessages);

        log.error("[HandleMethodArgumentNotValidException] Message: {}", errorMessage);

        return ResponseEntity.badRequest().body(Response.fail(METHOD_ARGUMENT_NOT_VALID.getCustomCode(), errorMessage));
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<Response<String>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("[NoResourceFoundException] URL = {}, Message = {}", e.getResourcePath(), e.getMessage());
        return new ResponseEntity<>(Response.fail(COMMON_RESOURCE_NOT_FOUND.getCustomCode(),
                COMMON_RESOURCE_NOT_FOUND.getMessage()), NOT_FOUND);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<Response<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("[HttpMessageNotReadableException] Message: {}", e.getMessage());
        return ResponseEntity.badRequest().body(Response.fail(COMMON_JSON_PROCESSING_ERROR.getCustomCode(),
                COMMON_JSON_PROCESSING_ERROR.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Response<String>> handleException(Exception e) {
        log.error("[Exception] Message: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body(Response.fail(COMMON_SYSTEM_ERROR.getCustomCode(),
                COMMON_SYSTEM_ERROR.getMessage()));
    }
}
