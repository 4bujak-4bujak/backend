package com.example.sabujak.post.service;

import com.example.sabujak.common.redis.service.RedisService;
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

    private static final String POST_VIEW_PREFIX = "post_view: ";
    private static final String POST_VIEW_VALUE = "viewed";
    private static final long POST_VIEW_EXPIRATION = 1000L * 60 * 60 * 24;

    private final RedisService redisService;
    private final PostRepository postRepository;

    public CustomSlice<Post> findPostsByCategory(Category child, Long cursorId, Pageable pageable) {
        log.info("Getting Posts. Child Category: [{}]", child);
        List<Post> posts = postRepository.findAllWithMembersAndImagesByCategoryAndId(child, cursorId, pageable);
        return createPostSlice(posts);
    }

    public CustomSlice<Post> findPostsByCategories(Category parent, Long cursorId, Pageable pageable) {
        List<Category> children = parent.getChild();
        log.info("Getting Posts. Parent Category: [{}], Children Category: [{}]", parent, children);
        List<Post> posts = postRepository.findAllWithMembersAndImagesByCategoriesAndId(children, cursorId, pageable);
        return createPostSlice(posts);
    }

    public CustomSlice<Post> createPostSlice(List<Post> posts) {
        boolean hasNext = posts.size() > ORIGIN_POST_PAGE_SIZE;
        log.info("Get [{}] Posts. One More Retrieved: [{}]", posts.size(), hasNext);
        if (hasNext) {
            posts = posts.stream()
                    .limit(ORIGIN_POST_PAGE_SIZE)
                    .collect(Collectors.toList());
        }
        return new CustomSlice<>(posts, hasNext);
    }

    public Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }

    public Post findPostWithMember(Long postId) {
        return postRepository.findWithMemberById(postId)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }

    public Post findPostWithMemberAndImages(Long postId) {
        return postRepository.findWithMemberAndImagesById(postId)
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

    public void increaseCommentCount(Post post) {
        post.increaseCommentCount();
    }

    public void decreaseLikeCount(Post post) {
        post.decreaseLikeCount();
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

    public boolean isViewed(Long postId, String viewerEmail) {
        String key = String.format("%s:%d:%s", POST_VIEW_PREFIX, postId, viewerEmail);
        return redisService.get(key, String.class).isPresent();
    }

    public void setViewed(Long postId, String viewerEmail) {
        String key = String.format("%s:%d:%s", POST_VIEW_PREFIX, postId, viewerEmail);
        redisService.set(key, POST_VIEW_VALUE, POST_VIEW_EXPIRATION);
    }
}
