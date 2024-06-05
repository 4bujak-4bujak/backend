package com.example.sabujak.notification.repository;

import com.example.sabujak.notification.entity.Notification;
import com.example.sabujak.notification.entity.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(
            value = "select n " +
                    "from Notification n " +
                    "where n.type = :type " +
                    "and (:cursorId is null or n.id < :cursorId) " +
                    "and n.member.memberEmail = :email"
    )
    List<Notification> findByMemberEmailAndCursorId(
            @Param("type") NotificationType type,
            @Param("cursorId") Long cursorId,
            @Param("email") String email,
            Pageable pageable
    );
}
