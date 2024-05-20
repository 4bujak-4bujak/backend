package com.example.sabujak.member.service;

import com.example.sabujak.member.dto.request.MemberRequestDto;
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
        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        return MemberResponseDto.AllInformation.of(member);

    }

    @Transactional
    public void changeMyPassword(String email, MemberRequestDto.ChangePassword passwordDto) {
        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        if(!bCryptPasswordEncoder.matches(passwordDto.currentPassword(), member.getMemberPassword())) {
            throw new AuthException(INVALID_CURRENT_PASSWORD);
        }
        member.changeMemberPassword(bCryptPasswordEncoder.encode(passwordDto.newPassword()));
    }


    public MemberResponseDto.NameAndCompany getMemberNameAndCompany(String email) {
        final Member member = memberRepository.findWithCompanyByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        return MemberResponseDto.NameAndCompany.of(member);
    }
}
