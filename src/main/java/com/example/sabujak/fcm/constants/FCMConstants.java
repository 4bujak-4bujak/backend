package com.example.sabujak.fcm.constants;

public class FCMConstants {

    // FCM Token
    public static final String FCM_TOKEN_PREFIX = "fcm_token:";
    public static final long FCM_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 60;

    // Target URL
    public static final String TARGET_URL_KEY = "targetUrl";
    public static final String COMMUNITY_URL_PREFIX = "/community/";

    // Notification Title
    public static final String DEFAULT_TITLE = "offispace";
    public static final String MEETING_ROOM_INVITATION_TITLE = "참여자 등록";

    // Notification Image
    public static final String DEFAULT_IMAGE = "https://sabujak-image-bucket.s3.ap-northeast-2.amazonaws.com/offispace+icon.png";
}
