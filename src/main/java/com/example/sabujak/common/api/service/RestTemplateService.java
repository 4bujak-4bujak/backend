package com.example.sabujak.common.api.service;

import org.springframework.http.HttpHeaders;

import java.util.Optional;

public interface RestTemplateService {

    <T> Optional<T> get(String url, HttpHeaders headers, Class<T> responseType);

    <T, R> Optional<T> post(String url, HttpHeaders headers, R body, Class<T> type);
}
