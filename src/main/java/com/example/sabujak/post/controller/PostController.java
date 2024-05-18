package com.example.sabujak.post.controller;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.post.dto.PostSaveRequest;
import com.example.sabujak.post.dto.PostSaveResponse;
import com.example.sabujak.post.entity.Category;
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

    @PostMapping
    public ResponseEntity<Response<PostSaveResponse>> savePost(
            @RequestParam Category category,
            @RequestBody @Validated PostSaveRequest postSaveRequest,
            @AuthenticationPrincipal AuthRequestDto.Access access
    ) {
        String email = access.getEmail();
        return ResponseEntity.ok(Response.success(postService.savePost(category, postSaveRequest, email)));
    }
}
