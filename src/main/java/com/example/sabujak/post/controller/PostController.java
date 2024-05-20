package com.example.sabujak.post.controller;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.post.dto.*;
import com.example.sabujak.post.dto.SaveCommentRequest;
import com.example.sabujak.post.dto.SavePostLikeRequest;
import com.example.sabujak.post.dto.SavePostRequest;
import com.example.sabujak.post.service.facade.PostFacade;
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

    private final PostFacade postFacade;

    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal AuthRequestDto.Access access
    ) {
        String email = access != null ? access.getEmail() : null;
        return ResponseEntity.ok(Response.success(postFacade.getPost(postId, email)));
    }

    @PostMapping
    public ResponseEntity<Response<SavePostResponse>> savePost(
            @RequestBody @Validated SavePostRequest savePostRequest,
            @AuthenticationPrincipal AuthRequestDto.Access access
    ) {
        String email = access.getEmail();
        return ResponseEntity.ok(Response.success(postFacade.savePost(savePostRequest, email)));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Response<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal AuthRequestDto.Access access
    ) {
        String email = access.getEmail();
        postFacade.deletePost(postId, email);
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/like")
    public ResponseEntity<Response<Void>> savePostLike(
            @RequestBody @Validated SavePostLikeRequest savePostLikeRequest,
            @AuthenticationPrincipal AuthRequestDto.Access access
    ) {
        String email = access.getEmail();
        postFacade.savePostLike(savePostLikeRequest, email);
        return ResponseEntity.ok(Response.success());
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Response<Void>> deletePostLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal AuthRequestDto.Access access
    ) {
        String email = access.getEmail();
        postFacade.deletePostLike(postId, email);
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Response<Void>> saveComment(
            @PathVariable Long postId,
            @RequestBody @Validated SaveCommentRequest saveCommentRequest,
            @AuthenticationPrincipal AuthRequestDto.Access access
    ) {
        String email = access.getEmail();
        postFacade.saveComment(postId, saveCommentRequest, email);
        return ResponseEntity.ok(Response.success());
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Response<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal AuthRequestDto.Access access
    ) {
        String email = access.getEmail();
        postFacade.deleteComment(postId, commentId, email);
        return ResponseEntity.ok(Response.success());
    }
}
