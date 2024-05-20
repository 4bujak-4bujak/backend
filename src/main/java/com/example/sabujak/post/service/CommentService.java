package com.example.sabujak.post.service;

import com.example.sabujak.post.entity.Comment;
import com.example.sabujak.post.repository.CommentRepository;
import com.example.sabujak.post.exception.PostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.sabujak.post.exception.PostErrorCode.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

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
        return !requester.equals(writer);
    }

    public void isWriterOrThrow(String requester, String writer) {
        if (!isWriter(requester, writer)) {
            throw new PostException(COMMENT_DELETE_DENIED);
        }
    }
}
