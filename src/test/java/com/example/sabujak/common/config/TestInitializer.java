package com.example.sabujak.common.config;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.security.jwt.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

import static com.example.sabujak.member.entity.Job.OWNER;

@ExtendWith(DatabaseClearExtension.class)
public abstract class TestInitializer extends Documentation {

    private static final String EMAIL = "test@email.com";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String PHONE_NUMBER = "010-0000-0000";
    private static final String AUTHORITY = "ROLE_ADMIN";

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil tokenUtil;

    public Member member;
    public String accessToken;

    @BeforeEach
    void init(){
        member = saveMember();
        accessToken = createAccessToken();
    }

    private Member saveMember() {
        String encodePassword = passwordEncoder.encode(PASSWORD);

        Member member = Member.builder()
                .memberEmail(EMAIL)
                .memberPassword(encodePassword)
                .memberName(NAME)
                .memberPhone(PHONE_NUMBER)
                .memberJob(OWNER)
                .memberSmsAgree(true)
                .build();

        return memberRepository.save(member);
    }

    private String createAccessToken() {
        return tokenUtil.generateAccessToken(Instant.now(), EMAIL, AUTHORITY);
    }
}
