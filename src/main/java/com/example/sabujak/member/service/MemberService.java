package com.example.sabujak.member.service;

import com.example.sabujak.member.dto.response.MemberResponseDto;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.security.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.sabujak.security.exception.AuthErrorCode.ACCOUNT_NOT_EXISTS;
import static com.example.sabujak.security.exception.AuthErrorCode.INVALID_CURRENT_PASSWORD;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberResponseDto.AllInformation getMyInfo(String email) {
        final Member member = memberRepository.findWithCompanyAndImageByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        return MemberResponseDto.AllInformation.from(member);

    }

    @Transactional
    public void signOut(String email) {
        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        member.signOut();
    }

    public void verifyPassword(String email, String password) {
        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        if(!bCryptPasswordEncoder.matches(password, member.getMemberPassword())) {
            throw new AuthException(INVALID_CURRENT_PASSWORD);
        }
    }

    @Transactional
    public void changeMyPassword(String email, String newPassword) {
        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        member.changeMemberPassword(bCryptPasswordEncoder.encode(newPassword));
    }
}
