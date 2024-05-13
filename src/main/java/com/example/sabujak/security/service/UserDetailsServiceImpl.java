package com.example.sabujak.security.service;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.security.dto.request.UserDetailsDto;
import com.example.sabujak.security.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.example.sabujak.security.exception.AuthErrorCode.INVALID_EMAIL_OR_PASSWORD;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberEmail) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new AuthException(INVALID_EMAIL_OR_PASSWORD));
        return new UserDetailsDto(member);
    }
}
