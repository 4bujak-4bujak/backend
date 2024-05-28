package com.example.sabujak.fcm.controller;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.fcm.dto.SaveFCMTokenRequest;
import com.example.sabujak.fcm.service.FCMTokenService;
import com.example.sabujak.security.dto.request.AuthRequestDto.Access;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fcm-token")
@RequiredArgsConstructor
public class FCMTokenController {

    private final FCMTokenService FCMTokenService;

    @PostMapping
    public ResponseEntity<Response<Void>> saveFCMToken(
            @RequestBody @Validated SaveFCMTokenRequest saveFCMTokenRequest,
            @AuthenticationPrincipal Access access
    ) {
        FCMTokenService.saveFCMToken(saveFCMTokenRequest, access.getEmail());
        return ResponseEntity.ok(Response.success());
    }

    @DeleteMapping
    public ResponseEntity<Response<Void>> removeFCMToken(
            @AuthenticationPrincipal Access access
    ) {
        FCMTokenService.removeFCMToken(access.getEmail());
        return ResponseEntity.ok(Response.success());
    }
}
