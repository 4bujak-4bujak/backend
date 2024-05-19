package com.example.sabujak.post.service;

import com.example.sabujak.common.config.TestInitializer;
import com.example.sabujak.post.dto.SavePostLikeRequest;
import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.sabujak.common.utils.PostUtils.createPost;

public class PostLikeConcurrencyTest extends TestInitializer {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("좋아요_동시성_테스트")
    void PostLikeConcurrency() throws InterruptedException {
        Post post = savePost();
        SavePostLikeRequest request = new SavePostLikeRequest(post.getId());

        int numberOfThreads = 1000;

        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    postService.savePostLike(request, "test@email.com");
                }
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        System.out.println("증가된 좋아요 수:" + postRepository.findById(post.getId()).orElseThrow().getLikeCount());
    }

    private Post savePost() {
        return postRepository.save(createPost());
    }
}
