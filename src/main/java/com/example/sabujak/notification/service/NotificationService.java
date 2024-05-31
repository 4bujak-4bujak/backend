package com.example.sabujak.notification.service;

import com.example.sabujak.notification.entity.Notification;
import com.example.sabujak.notification.repository.NotificationRepository;
import com.example.sabujak.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(propagation = REQUIRED)
    public void saveNotification(String content, String targetUrl, Member member) {
        Notification notification = Notification.builder()
                .content(content)
                .targetUrl(targetUrl)
                .build();
        notification.setMember(member);
        notificationRepository.save(notification);
        log.info("Saved Notification. Notification ID: [{}]", notification.getId());
    }
}
