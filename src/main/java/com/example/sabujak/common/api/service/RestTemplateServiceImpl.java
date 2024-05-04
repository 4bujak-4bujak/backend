package com.example.sabujak.common.api.service;

import com.example.sabujak.common.exception.CustomException;
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

import static com.example.sabujak.common.exception.ErrorCode.COMMON_SYSTEM_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestTemplateServiceImpl implements RestTemplateService {

    private final RestTemplate restTemplate;

    @Override
    public <T> Optional<T> get(String url, HttpHeaders headers, Class<T> responseType) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error(
                    "[RestTemplate GET Error] Status Code: {} Message: {} Error Response Body: {}",
                    e.getStatusCode(), e.getMessage(), e.getResponseBodyAsString()
            );
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("[RestTemplate GET Error] Message: {}", e.getMessage(), e);
            throw new CustomException(COMMON_SYSTEM_ERROR);
        }
    }
}
