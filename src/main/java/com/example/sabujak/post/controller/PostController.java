package com.example.sabujak.post.controller;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.post.dto.PostResponse;
import com.example.sabujak.post.dto.SavePostLikeRequest;
import com.example.sabujak.post.dto.SavePostRequest;
import com.example.sabujak.post.dto.SavePostResponse;
import com.example.sabujak.post.service.PostService;
import com.example.sabujak.security.dto.request.AuthRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal AuthRequestDto.Access access
    ) {
        String email = access != null ? access.getEmail() : null;
        return ResponseEntity.ok(Response.success(postService.getPost(postId, email)));
    }

    @PostMapping
    public ResponseEntity<Response<SavePostResponse>> savePost(
            @RequestBody @Validated SavePostRequest savePostRequest,
            @AuthenticationPrincipal AuthRequestDto.Access access
    ) {
        String email = access.getEmail();
        return ResponseEntity.ok(Response.success(postService.savePost(savePostRequest, email)));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Response<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal AuthRequestDto.Access access
    ) {
        String email = access.getEmail();
        postService.deletePost(postId, email);
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/like")
    public ResponseEntity<Response<Void>> savePostLike(
            @RequestBody @Validated SavePostLikeRequest savePostLikeRequest,
            @AuthenticationPrincipal AuthRequestDto.Access access
    ) {
        String email = access.getEmail();
        postService.savePostLike(savePostLikeRequest, email);
        return ResponseEntity.ok(Response.success());
    }
}
