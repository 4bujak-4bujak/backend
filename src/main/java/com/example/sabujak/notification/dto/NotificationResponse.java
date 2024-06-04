package com.example.sabujak.notification.dto;

import com.example.sabujak.notification.entity.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(
        String content,
        String targetUrl,
        LocalDateTime date

) {
    public static NotificationResponse of(Notification notification) {
        return new NotificationResponse(
                notification.getContent(),
                notification.getTargetUrl(),
                notification.getCreatedDate()
        );
    }
}
