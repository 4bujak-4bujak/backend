package com.example.sabujak.post.service;

import com.example.sabujak.post.dto.CustomSlice;
import com.example.sabujak.post.entity.Category;
import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.exception.PostException;
import com.example.sabujak.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.sabujak.post.constants.PaginationConstants.ORIGIN_POST_PAGE_SIZE;
import static com.example.sabujak.post.exception.PostErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public CustomSlice<Post> findPosts(Category category, Long cursorId, Pageable pageable) {
        List<Post> posts = postRepository.findAllWithMembersByCategoryAndId(category, cursorId, pageable);
        boolean hasNext = posts.size() > ORIGIN_POST_PAGE_SIZE;
        log.info("Get [{}] Posts. One More Retrieved: [{}]", posts.size(), hasNext);
        if (hasNext) {
            posts = posts.stream()
                    .limit(ORIGIN_POST_PAGE_SIZE)
                    .collect(Collectors.toList());
        }
        return new CustomSlice<>(posts, hasNext);
    }

    public Post findByIdWithPessimisticLock(Long postId) {
        return postRepository.findByIdWithPessimisticLock(postId)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }

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
