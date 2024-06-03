package com.example.sabujak.notification.utils;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class NotificationContent {

    private static final String COMMENT_NOTIFICATION_FORMAT = "%s에 %s님이 댓글을 남겼습니다.";

    public static String createCommentContent(String title, String nickName) {
        return String.format(COMMENT_NOTIFICATION_FORMAT, title, nickName);
    }
}
