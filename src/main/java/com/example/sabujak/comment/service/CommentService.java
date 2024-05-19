package com.example.sabujak.comment.service;

import com.example.sabujak.comment.dto.SaveCommentRequest;
import com.example.sabujak.comment.entity.Comment;
import com.example.sabujak.comment.repository.CommentRepository;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.exception.PostException;
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
public class CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void saveComment(Long postId, SaveCommentRequest saveCommentRequest, String email) {
        Member member = findMember(email);
        Post post = findPost(postId);

        post.increaseCommentCount();

        Comment comment = Comment.builder()
                .content(saveCommentRequest.content())
                .build();
        comment.setPost(post);
        comment.setMember(member);

        commentRepository.save(comment);
        log.info("Comment saved successfully. Post ID: [{}] Member Email: [{}]", postId, email);
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
