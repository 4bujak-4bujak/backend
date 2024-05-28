package com.example.sabujak.member.service;

import com.example.sabujak.image.service.ImageService;
import com.example.sabujak.member.dto.response.MemberResponseDto;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.entity.MemberImage;
import com.example.sabujak.member.repository.MemberImageRepository;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.security.exception.AuthException;
import io.jsonwebtoken.lang.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import static com.example.sabujak.security.exception.AuthErrorCode.ACCOUNT_NOT_EXISTS;
import static com.example.sabujak.security.exception.AuthErrorCode.INVALID_CURRENT_PASSWORD;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ImageService imageService;
    private final MemberImageRepository memberImageRepository;

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

    public Member findMember(String email) {
        return memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));
    }

    @Transactional
    public void changeMemberImage(String email, MultipartFile image) {
        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));

        MemberImage exImage = member.getImage(); // 이전 사용자 프로필 이미지
        String imageUrl = imageService.saveImage(image);

        MemberImage memberImage = MemberImage.builder()
                .imageUrl(imageUrl)
                .build();
        memberImageRepository.save(memberImage);
        member.setImage(memberImage);
        memberImage.setMember(member);

        if (exImage != null && exImage.getImageUrl() != null)  //이전 사용자 사진 삭제
            imageService.deleteImage(exImage.getImageUrl());

    }
}
