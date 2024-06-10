package com.example.sabujak.notification.dto;

import com.example.sabujak.notification.entity.Notification;
import com.example.sabujak.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long notificationId,
        String image,
        String title,
        String content,
        Long targetId,
        NotificationType targetType,
        LocalDateTime date

) {
    public static NotificationResponse of(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getImage(),
                notification.getTitle(),
                notification.getContent(),
                notification.getTargetId(),
                notification.getType(),
                notification.getCreatedDate()
        );
    }
}
