package com.example.sabujak.post.service;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.post.dto.PostSaveRequest;
import com.example.sabujak.post.dto.PostSaveResponse;
import com.example.sabujak.post.entity.Category;
import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.repository.PostRepository;
import com.example.sabujak.security.exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.sabujak.security.exception.AuthErrorCode.ACCOUNT_NOT_EXISTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public PostSaveResponse savePost(Category category, PostSaveRequest postSaveRequest, String email) {
        Member member = findMember(email);

        Post post = Post.builder()
                .category(category)
                .tag(postSaveRequest.tag())
                .title(postSaveRequest.title())
                .content(postSaveRequest.content())
                .build();
        post.setMember(member);

        log.info("Saving a new post for member with email: {}", email);

        return PostSaveResponse.of(postRepository.save(post));
    }

    public Member findMember(String email) {
        return memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));
    }
}
