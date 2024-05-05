package com.example.sabujak.common.api.service;

import com.example.sabujak.common.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.example.sabujak.common.exception.CommonErrorCode.COMMON_SYSTEM_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestTemplateServiceImpl implements RestTemplateService {

    private final RestTemplate restTemplate;

    @Override
    public <T> Optional<T> get(String url, HttpHeaders headers, Class<T> type) {
        try {
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, request, type);
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error(
                    "[RestTemplate GET Error] Status Code: {} Message: {} Error Response Body: {}",
                    e.getStatusCode(), e.getMessage(), e.getResponseBodyAsString()
            );
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("[RestTemplate GET Error] Message: {}", e.getMessage(), e);
            throw new CommonException(COMMON_SYSTEM_ERROR);
        }
    }

    @Override
    public <T, R> Optional<T> post(String url, HttpHeaders headers, R body, Class<T> type) {
        try {
            HttpEntity<R> request = new HttpEntity<>(body, headers);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, request, type);
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error(
                    "[RestTemplate POST Error] Status Code: {} Message: {} Error Response Body: {}",
                    e.getStatusCode(), e.getMessage(), e.getResponseBodyAsString()
            );
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("[RestTemplate POST Error] Message: {}", e.getMessage(), e);
            throw new CommonException(COMMON_SYSTEM_ERROR);
        }
    }
}
