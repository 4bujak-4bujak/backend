package com.example.sabujak.notification.service;

import com.example.sabujak.notification.dto.NotificationResponse;
import com.example.sabujak.notification.entity.Notification;
import com.example.sabujak.notification.entity.NotificationType;
import com.example.sabujak.notification.repository.NotificationRepository;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.post.dto.CustomSlice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.sabujak.post.constants.PaginationConstants.DEFAULT_PAGE;
import static com.example.sabujak.post.constants.PaginationConstants.ORIGIN_NOTIFICATION_PAGE_SIZE;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public CustomSlice<NotificationResponse> getNotification(NotificationType type, Long cursorId, String email, Pageable pageable) {
        log.info("Getting Notification. Cursor ID: [{}], Member Email: [{}]", cursorId, email);
        List<Notification> notifications = getNotifications(type, cursorId, email, pageable);
        boolean hasNext = notifications.size() > ORIGIN_NOTIFICATION_PAGE_SIZE;
        log.info("Get Notification. Size: [{}]. One More Found: [{}]", notifications.size(), hasNext);
        if (hasNext) {
            notifications = removeLastNotification(notifications);
            log.info("Removed Last Notification. Size: [{}]", notifications.size());
        }
        List<NotificationResponse> responses = createNotificationResponses(notifications);
        return new CustomSlice<>(responses, hasNext);
    }

    @Transactional(propagation = REQUIRED)
    public void saveNotification(String content, String targetUrl, NotificationType type, Member member) {
        Notification notification = Notification.builder()
                .content(content)
                .targetUrl(targetUrl)
                .type(type)
                .build();
        notification.setMember(member);
        notificationRepository.save(notification);
        log.info("Saved Notification. Notification ID: [{}]", notification.getId());
    }

    private List<Notification> getNotifications(NotificationType type, Long cursorId, String email, Pageable pageable) {
        return notificationRepository.findByMemberEmailAndCursorId(type, cursorId, email, pageable);
    }

    private List<Notification> removeLastNotification(List<Notification> notificationList) {
        return notificationList.subList(DEFAULT_PAGE, ORIGIN_NOTIFICATION_PAGE_SIZE);
    }

    private List<NotificationResponse> createNotificationResponses(List<Notification> notifications) {
        return notifications.stream().map(NotificationResponse::of).toList();
    }
}
