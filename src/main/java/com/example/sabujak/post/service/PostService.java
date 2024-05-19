package com.example.sabujak.post.service;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.post.dto.PostResponse;
import com.example.sabujak.post.dto.SavePostLikeRequest;
import com.example.sabujak.post.dto.SavePostRequest;
import com.example.sabujak.post.dto.SavePostResponse;
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

import java.util.Optional;

import static com.example.sabujak.post.exception.PostErrorCode.*;
import static com.example.sabujak.security.exception.AuthErrorCode.ACCOUNT_NOT_EXISTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId, String viewerEmail) {
        Post post = findPostWithMember(postId);
        post.increaseViewCount();

        Member member = post.getMember();
        String writerEmail = member.getMemberEmail();

        boolean isAuthenticated = viewerEmail != null;
        boolean isWriter = isAuthenticated && isWriter(writerEmail, viewerEmail);
        boolean isLiked = isAuthenticated && isLiked(postId, viewerEmail);

        log.info("Getting post. Post ID: [{}] Viewer Email: [{}]", postId, viewerEmail);

        return PostResponse.of(post, member, isWriter, isLiked);
    }

    @Transactional
    public SavePostResponse savePost(SavePostRequest savePostRequest, String email) {
        Member member = findMember(email);

        Post post = Post.builder()
                .category(savePostRequest.category())
                .tag(savePostRequest.tag())
                .title(savePostRequest.title())
                .content(savePostRequest.content())
                .build();
        post.setMember(member);

        log.info("Saving post. Member Email: [{}]", email);

        return SavePostResponse.of(postRepository.save(post));
    }

    @Transactional
    public void deletePost(Long postId, String email) {
        Post post = findPostWithMember(postId);
        String writerEmail = post.getMember().getMemberEmail();

        if (!writerEmail.equals(email)) {
            log.warn("Unauthorized attempt to delete post. Post ID: [{}], Member Email: [{}]", postId, email);
            throw new PostException(POST_DELETE_DENIED);
        }

        postRepository.delete(post);
        log.info("Post deleted successfully. Post ID: [{}], Member Email: [{}]", postId, email);
    }

    @Transactional
    public void savePostLike(SavePostLikeRequest savePostLikeRequest, String email) {
        Long postId = savePostLikeRequest.postId();

        Post post = findPost(postId);
        post.increaseLikeCount();

        PostLikeId postLikeId = createPostLikeId(postId, email);
        PostLike postLike = PostLike.builder()
                .id(postLikeId)
                .build();
        postLikeRepository.save(postLike);

        log.info("Like Post saved successfully. Post ID: [{}], Member Email: [{}]", post.getId(), email);
    }

    @Transactional
    public void deletePostLike(Long postId, String email) {
        Post post = findPost(postId);
        post.decreaseLikeCount();

        PostLikeId postLikeId = createPostLikeId(postId, email);
        PostLike postLike = findPostLike(postLikeId)
                .orElseThrow(() -> new PostException(POST_LIKE_NOT_FOUND));
        postLikeRepository.delete(postLike);

        log.info("Post Like deleted successfully. Post ID: [{}], Member Email: [{}]", postId, email);
    }

    private boolean isWriter(String writerEmail, String viewerEmail) {
        return viewerEmail.equals(writerEmail);
    }

    private boolean isLiked(Long postId, String viewerEmail) {
        PostLikeId postLikeId = createPostLikeId(postId, viewerEmail);
        return findPostLike(postLikeId).isPresent();
    }

    public PostLikeId createPostLikeId(Long postId, String memberEmail) {
        return PostLikeId.builder()
                .postId(postId)
                .memberEmail(memberEmail)
                .build();
    }

    public Member findMember(String email) {
        return memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));
    }

    public Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }

    public Post findPostWithMember(Long postId) {
        return postRepository.findWithMemberById(postId)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }

    public Optional<PostLike> findPostLike(PostLikeId postLikeId) {
        return postLikeRepository.findById(postLikeId);
    }
}
