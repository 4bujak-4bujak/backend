package com.example.sabujak.comment.controller;

import com.example.sabujak.comment.dto.SaveCommentRequest;
import com.example.sabujak.comment.service.CommentService;
import com.example.sabujak.common.response.Response;
import com.example.sabujak.security.dto.request.AuthRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Response<Void>> saveComment(
            @PathVariable Long postId,
            @RequestBody @Validated SaveCommentRequest saveCommentRequest,
            @AuthenticationPrincipal AuthRequestDto.Access access
    ) {
        String email = access.getEmail();
        commentService.saveComment(postId, saveCommentRequest, email);
        return ResponseEntity.ok(Response.success());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Response<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal AuthRequestDto.Access access
    ) {
        String email = access.getEmail();
        commentService.deleteComment(postId, commentId, email);
        return ResponseEntity.ok(Response.success());
    }
}
