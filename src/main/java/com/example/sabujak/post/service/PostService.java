package com.example.sabujak.post.service;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.post.dto.PostLikeSaveRequest;
import com.example.sabujak.post.dto.PostSaveRequest;
import com.example.sabujak.post.dto.PostSaveResponse;
import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.entity.PostLike;
import com.example.sabujak.post.entity.PostLikeId;
import com.example.sabujak.post.exception.PostException;
import com.example.sabujak.post.repository.PostLikeRepository;
import com.example.sabujak.post.repository.PostRepository;
import com.example.sabujak.security.exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.sabujak.post.exception.PostErrorCode.POST_NOT_FOUND;
import static com.example.sabujak.security.exception.AuthErrorCode.ACCOUNT_NOT_EXISTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public PostSaveResponse savePost(PostSaveRequest postSaveRequest, String email) {
        Member member = findMember(email);

        Post post = Post.builder()
                .category(postSaveRequest.category())
                .tag(postSaveRequest.tag())
                .title(postSaveRequest.title())
                .content(postSaveRequest.content())
                .build();
        post.setMember(member);

        log.info("Saving post for member with email: [{}]", email);

        return PostSaveResponse.of(postRepository.save(post));
    }

    @Transactional
    public void savePostLike(PostLikeSaveRequest postLikeSaveRequest, String email) {
        Member member = findMember(email);

        Post post = findPost(postLikeSaveRequest.postId());
        post.increaseLikeCount();

        PostLike postLike = PostLike.builder()
                .id(
                        PostLikeId.builder()
                                .postId(post.getId())
                                .memberId(member.getMemberId())
                                .build()
                )
                .build();
        postLikeRepository.save(postLike);

        log.info("Saved post like for member with email: [{}] post ID: [{}]", email, post.getId());
    }

    public Member findMember(String email) {
        return memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));
    }

    public Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }
}
