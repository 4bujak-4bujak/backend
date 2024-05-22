package com.example.sabujak.post.service;

import com.example.sabujak.post.dto.CustomSlice;
import com.example.sabujak.post.entity.Comment;
import com.example.sabujak.post.repository.CommentRepository;
import com.example.sabujak.post.exception.PostException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.sabujak.post.constants.PaginationConstants.ORIGIN_COMMENT_PAGE_SIZE;
import static com.example.sabujak.post.exception.PostErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public CustomSlice<Comment> findComments(Long postId, Long cursorId, Pageable pageable) {
        List<Comment> comments = commentRepository.findWithMemberByPostIdAndCursorId(postId, cursorId,pageable);
        boolean hasNext = comments.size() > ORIGIN_COMMENT_PAGE_SIZE;
        log.info("Get [{}] Comments. One More Retrieved: [{}]", comments.size(), hasNext);
        if(hasNext) {
            comments = comments.stream()
                    .limit(ORIGIN_COMMENT_PAGE_SIZE)
                    .collect(Collectors.toList());
        }
        return new CustomSlice<>(comments, hasNext);
    }

    public Comment findCommentWithMemberAndPost(Long postId, Long commentId) {
        return commentRepository.findWithMemberAndPostByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new PostException(COMMENT_NOT_FOUND));
    }

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    public boolean isWriter(String requester, String writer) {
        return requester.equals(writer);
    }

    public void isWriterOrThrow(String requester, String writer) {
        if (!isWriter(requester, writer)) {
            throw new PostException(COMMENT_DELETE_DENIED);
        }
    }
}
