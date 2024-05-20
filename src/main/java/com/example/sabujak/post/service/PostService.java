package com.example.sabujak.post.service;

import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.exception.PostException;
import com.example.sabujak.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static com.example.sabujak.post.exception.PostErrorCode.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }

    public Post findPostWithMember(Long postId) {
        return postRepository.findWithMemberById(postId)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }

    public void savePost(Post post) {
        postRepository.save(post);
    }

    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    public void increaseViewCount(Post post) {
        post.increaseViewCount();
    }

    public void increaseLikeCount(Post post) {
        post.increaseLikeCount();
    }

    public void decreaseLikeCount(Post post) {
        post.decreaseLikeCount();
    }

    public void increaseCommentCount(Post post) {
        post.increaseCommentCount();
    }

    public void decreaseCommentCount(Post post) {
        post.decreaseCommentCount();
    }

    public boolean isWriter(String requester, String writer) {
        return requester.equals(writer);
    }

    public void isWriterOrThrow(String requester, String writer) {
        if (!isWriter(requester, writer)) {
            throw new PostException(POST_DELETE_DENIED);
        }
    }
}
