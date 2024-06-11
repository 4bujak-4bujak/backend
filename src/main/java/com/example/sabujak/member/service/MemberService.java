package com.example.sabujak.member.service;

import com.example.sabujak.image.service.ImageService;
import com.example.sabujak.member.dto.response.MemberModalIgnoredResponseDto;
import com.example.sabujak.member.dto.response.MemberResponseDto;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.entity.MemberImage;
import com.example.sabujak.member.repository.MemberImageRepository;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.security.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;

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

    private final String defaultMemberImageUrl = "https://bzbz-file-bucket.s3.ap-northeast-2.amazonaws.com/Member-Default-Image.png";

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

        if (exImage != null && exImage.getImageUrl() != null && !exImage.getImageUrl().equals(defaultMemberImageUrl))  //이전 사용자 사진 삭제
            imageService.deleteImage(exImage.getImageUrl());

    }

    @Transactional
    public void changeModalIgnored(String email){
        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));
        LocalDateTime now = LocalDateTime.now();
        member.changeModalIgnoredAndTime(now);
    }

    public MemberModalIgnoredResponseDto checkModalIgnored(String email){
        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));
        LocalDateTime now = LocalDateTime.now();

        if (member.getMemberModalIgnoredTime() == null)
            return MemberModalIgnoredResponseDto.of(member.getMemberId(), false);

        if (member.isMemberModalIgnored()){
            long betweenHours = Duration.between(now, member.getMemberModalIgnoredTime()).toHours();
            if (betweenHours < 24L)
                return MemberModalIgnoredResponseDto.of(member.getMemberId(), true);
        }
        return MemberModalIgnoredResponseDto.of(member.getMemberId(), false);
    }


}
