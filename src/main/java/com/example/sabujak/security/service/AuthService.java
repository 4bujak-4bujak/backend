package com.example.sabujak.security.service;

import com.example.sabujak.member.dto.request.MemberRequestDto;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.security.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.sabujak.security.exception.AuthErrorCode.EMAIL_ALREADY_EXISTS;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void signUp(MemberRequestDto.SignUp signUp) {
        if (memberRepository.existsByMemberEmail(signUp.memberEmail())) {
            throw new AuthException(EMAIL_ALREADY_EXISTS);
        }

        String encryptedPassword = bCryptPasswordEncoder.encode(signUp.memberPassword());

        memberRepository.save(signUp.toEntity(encryptedPassword));
    }
}
