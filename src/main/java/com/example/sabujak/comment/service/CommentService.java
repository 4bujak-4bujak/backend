package com.example.sabujak.comment.service;

import com.example.sabujak.comment.dto.SaveCommentRequest;
import com.example.sabujak.comment.entity.Comment;
import com.example.sabujak.comment.exception.CommentException;
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

import static com.example.sabujak.comment.exception.CommentErrorCode.COMMENT_DELETE_DENIED;
import static com.example.sabujak.comment.exception.CommentErrorCode.COMMENT_NOT_FOUND;
import static com.example.sabujak.post.exception.PostErrorCode.*;
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
        Post post = findPost(postId);
        post.increaseCommentCount();

        Member member = findMember(email);

        Comment comment = Comment.builder()
                .content(saveCommentRequest.content())
                .build();
        comment.setMember(member);
        comment.setPost(post);

        commentRepository.save(comment);
        log.info("Comment saved successfully. Post ID: [{}] Member Email: [{}]", postId, email);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, String email) {
        Comment comment = findCommentWithMemberAndPost(postId, commentId);

        Post post = comment.getPost();
        post.decreaseCommentCount();

        Member member = comment.getMember();
        String writerEmail = member.getMemberEmail();

        if (!writerEmail.equals(email)) {
            log.warn("Unauthorized attempt to delete comment. Comment ID: [{}], Member Email: [{}]", commentId, email);
            throw new CommentException(COMMENT_DELETE_DENIED);
        }

        commentRepository.delete(comment);
        log.info("Comment deleted successfully. Comment ID: [{}], Member Email: [{}]", commentId, email);
    }

    public Member findMember(String email) {
        return memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(ACCOUNT_NOT_EXISTS));
    }

    public Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }

    private Comment findCommentWithMemberAndPost(Long postId, Long commentId) {
        return commentRepository.findWithMemberAndPostByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new CommentException(COMMENT_NOT_FOUND));
    }
}
