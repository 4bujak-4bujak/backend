package com.example.sabujak.post.service;

import com.example.sabujak.post.entity.PostLike;
import com.example.sabujak.post.entity.PostLikeId;
import com.example.sabujak.post.exception.PostException;
import com.example.sabujak.post.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.sabujak.post.exception.PostErrorCode.POST_LIKE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;

    public PostLikeId createPostLikeId(Long postId, String memberEmail) {
        return PostLikeId.builder()
                .postId(postId)
                .memberEmail(memberEmail)
                .build();
    }

    public PostLike createPostLike(PostLikeId postLikeId) {
        return PostLike.builder()
                .id(postLikeId)
                .build();
    }

    public Optional<PostLike> findPostLike(PostLikeId postLikeId) {
        return postLikeRepository.findById(postLikeId);
    }

    public PostLike findPostLikeOrThrow(PostLikeId postLikeId) {
        return postLikeRepository.findById(postLikeId)
                .orElseThrow(() -> new PostException(POST_LIKE_NOT_FOUND));
    }

    public void savePostLike(PostLike postLike) {
        postLikeRepository.save(postLike);
    }

    public void deletePostLike(PostLike postLike) {
        postLikeRepository.delete(postLike);
    }

    public boolean isLiked(Long postId, String memberEmail) {
        PostLikeId postLikeId = createPostLikeId(postId, memberEmail);
        return findPostLike(postLikeId).isPresent();
    }
}
