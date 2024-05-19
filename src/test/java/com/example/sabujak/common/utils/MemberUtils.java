package com.example.sabujak.common.utils;

import com.example.sabujak.member.entity.Member;

import static com.example.sabujak.member.entity.Job.CONTENTS;

public class MemberUtils {

    private static final String INVALID_EMAIL = "InvalidEmail@email.com";
    private static final String INVALID_PASSWORD = "InvalidPassword";
    private static final String INVALID_NAME = "InvalidName";
    private static final String INVALID_PHONE_NUMBER = "010-1234-5678";

    private MemberUtils() {

    }

    public static Member createInvaildMember() {
        return Member.builder()
                .memberEmail(INVALID_EMAIL)
                .memberPassword(INVALID_PASSWORD)
                .memberName(INVALID_NAME)
                .memberPhone(INVALID_PHONE_NUMBER)
                .memberJob(CONTENTS)
                .memberSmsAgree(true)
                .build();
    }
}
