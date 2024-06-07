package com.example.sabujak.privatepost.service;

import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.branch.exception.BranchErrorCode;
import com.example.sabujak.branch.exception.BranchException;
import com.example.sabujak.branch.repository.BranchRepository;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.privatepost.dto.request.SavePrivatePostRequestDto;
import com.example.sabujak.privatepost.dto.response.PrivatePostResponseDto;
import com.example.sabujak.privatepost.dto.response.SavePrivatePostResponseDto;
import com.example.sabujak.privatepost.entity.PrivatePost;
import com.example.sabujak.privatepost.exception.PrivatePostErrorCode;
import com.example.sabujak.privatepost.exception.PrivatePostException;
import com.example.sabujak.privatepost.repository.PrivatePostRepository;
import com.example.sabujak.security.exception.AuthErrorCode;
import com.example.sabujak.security.exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PrivatePostService {

    private final PrivatePostRepository privatePostRepository;
    private final MemberRepository memberRepository;
    private final BranchRepository branchRepository;

    @Transactional
    public SavePrivatePostResponseDto savePrivatePost(SavePrivatePostRequestDto savePrivatePostRequestDto, String email) {
        Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(AuthErrorCode.ACCOUNT_NOT_EXISTS));
        Branch branch = branchRepository.findByBranchName(savePrivatePostRequestDto.branchName())
                .orElseThrow(() -> new BranchException(BranchErrorCode.ENTITY_NOT_FOUND_BY_NAME));
        PrivatePost privatePost = savePrivatePostRequestDto.toEntity(member,branch);
        privatePostRepository.save(privatePost);
        member.getPrivatePosts().add(privatePost);
        branch.getPrivatePosts().add(privatePost);

        log.info("[PrivatePostService savePrivatePost] Success! memberName: {}, branchName: {}, PrivatePostId: {}", member.getMemberName(), savePrivatePostRequestDto.branchName(), privatePost.getId());
        return SavePrivatePostResponseDto.of(privatePost);
    }

    public Slice<PrivatePostResponseDto> getPrivatePosts(Pageable pageable, Long cursorId, String email){
        Slice<PrivatePost> privatePosts = privatePostRepository.findBySlice(cursorId, pageable, email);
        List<PrivatePostResponseDto> postResponseDtoList = privatePosts.getContent().stream().map(PrivatePostResponseDto::of).toList();


        log.info("[PrivatePostService getPrivatePosts] Success! email: {}, cursorId: {}", email, cursorId);
        return new SliceImpl<>(postResponseDtoList, pageable, privatePosts.hasNext());

    }

    public PrivatePostResponseDto getPrivatePost(Long privatePostId, String email) {
        PrivatePost privatePost = privatePostRepository.findById(privatePostId)
                .orElseThrow(() -> new PrivatePostException(PrivatePostErrorCode.PRIVATEPOST_NOT_FOUND));

        if (!memberRepository.existsByMemberEmail(email))
            throw new AuthException(AuthErrorCode.ACCOUNT_NOT_EXISTS);

        String writerEmail = privatePost.getMember().getMemberEmail();

        if (!email.equals(writerEmail)) {
            log.error("[PrivatePostService getPrivatePost] Error occurred! writerEmail: {}, viewerEmail: {}", writerEmail, email);
            throw new PrivatePostException(PrivatePostErrorCode.PRIVATEPOST_GET_DENIED);
        }
        log.info("[PrivatePostService getPrivatePost] Success! privatePostId: {}", privatePostId);
        return PrivatePostResponseDto.of(privatePost);
    }

    @Transactional
    public void deletePrivatePost(Long privatePostId, String email){
        PrivatePost privatePost = privatePostRepository.findById(privatePostId)
                .orElseThrow(() -> new PrivatePostException(PrivatePostErrorCode.PRIVATEPOST_NOT_FOUND));

        if (!memberRepository.existsByMemberEmail(email))
            throw new AuthException(AuthErrorCode.ACCOUNT_NOT_EXISTS);

        String deleterEmail = privatePost.getMember().getMemberEmail();

        if (!email.equals(deleterEmail)) {
            log.error("[PrivatePostService deletePrivatePost] Error occurred! writerEmail: {}, viewerEmail: {}", deleterEmail, email);
            throw new PrivatePostException(PrivatePostErrorCode.PRIVATEPOST_DELETE_DENIED);
        }

        privatePostRepository.delete(privatePost);
        log.info("[PrivatePostService deletePrivatePost] Success! privatePostId: {}", privatePostId);
    }


}

