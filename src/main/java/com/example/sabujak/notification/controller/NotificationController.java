package com.example.sabujak.notification.controller;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.notification.dto.NotificationResponse;
import com.example.sabujak.notification.entity.NotificationType;
import com.example.sabujak.notification.service.NotificationService;
import com.example.sabujak.post.dto.CustomSlice;
import com.example.sabujak.security.dto.request.AuthRequestDto.Access;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.sabujak.post.constants.PaginationConstants.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Response<CustomSlice<NotificationResponse>>> getNotification(
            @RequestParam NotificationType type,
            @RequestParam(required = false) Long cursorId,
            @PageableDefault(
                    page = DEFAULT_PAGE,
                    size = DEFAULT_NOTIFICATION_PAGE_SIZE,
                    sort = DEFAULT_SORT_FIELD,
                    direction = DESC
            ) Pageable pageable,
            @AuthenticationPrincipal Access access
    ) {
        String email = access.getEmail();
        return ResponseEntity.ok(Response.success(notificationService.getNotification(type, cursorId, email, pageable)));
    }
}
