package com.example.sabujak.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationContent {

    COMMENT("님이 회원의 게시글에 댓글을 남겼습니다."),
    LIKE("님이 회원의 게시글에 좋아요를 눌렀습니다.");

    private final String suffix;
}
