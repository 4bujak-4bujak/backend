package com.example.sabujak.post.controller;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.post.dto.*;
import com.example.sabujak.post.dto.SaveCommentRequest;
import com.example.sabujak.post.dto.SavePostLikeRequest;
import com.example.sabujak.post.dto.SavePostRequest;
import com.example.sabujak.post.entity.Category;
import com.example.sabujak.post.service.facade.PostFacade;
import com.example.sabujak.security.dto.request.AuthRequestDto.Access;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.sabujak.post.constants.PaginationConstants.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostFacade postFacade;

    @GetMapping
    public ResponseEntity<Response<CustomSlice<PostResponse>>> getPosts(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Long cursorId,
            @PageableDefault(
                    page = DEFAULT_PAGE,
                    size = DEFAULT_POST_PAGE_SIZE,
                    sort = DEFAULT_SORT_FIELD,
                    direction = DESC
            ) Pageable pageable,
            @AuthenticationPrincipal Access access
    ) {
        String email = getEmailOrNull(access);
        return ResponseEntity.ok(Response.success(postFacade.getPosts(category, cursorId, pageable, email)));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Access access
    ) {
        String email = getEmailOrNull(access);
        return ResponseEntity.ok(Response.success(postFacade.getPost(postId, email)));
    }

    @PostMapping
    public ResponseEntity<Response<SavePostResponse>> savePost(
            @RequestPart @Validated SavePostRequest savePostRequest,
            @RequestPart(required = false) MultipartFile[] images,
            @AuthenticationPrincipal Access access
    ) {
        String email = access.getEmail();
        return ResponseEntity.ok(Response.success(postFacade.savePost(savePostRequest, images, email)));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Response<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Access access
    ) {
        String email = access.getEmail();
        postFacade.deletePost(postId, email);
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/like")
    public ResponseEntity<Response<Void>> savePostLike(
            @RequestBody @Validated SavePostLikeRequest savePostLikeRequest,
            @AuthenticationPrincipal Access access
    ) {
        String email = access.getEmail();
        postFacade.savePostLike(savePostLikeRequest, email);
        return ResponseEntity.ok(Response.success());
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Response<Void>> deletePostLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal Access access
    ) {
        String email = access.getEmail();
        postFacade.deletePostLike(postId, email);
        return ResponseEntity.ok(Response.success());
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<Response<CustomSlice<CommentResponse>>> getComments(
            @PathVariable Long postId,
            @RequestParam(required = false) Long cursorId,
            @PageableDefault(
                    page = DEFAULT_PAGE,
                    size = DEFAULT_COMMENT_PAGE_SIZE,
                    sort = DEFAULT_SORT_FIELD,
                    direction = DESC
            ) Pageable pageable,
            @AuthenticationPrincipal Access access
    ) {
        String email = getEmailOrNull(access);
        return ResponseEntity.ok(Response.success(postFacade.getComments(postId, cursorId, pageable, email)));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Response<Void>> saveComment(
            @PathVariable Long postId,
            @RequestBody @Validated SaveCommentRequest saveCommentRequest,
            @AuthenticationPrincipal Access access
    ) {
        String email = access.getEmail();
        postFacade.saveComment(postId, saveCommentRequest, email);
        return ResponseEntity.ok(Response.success());
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Response<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal Access access
    ) {
        String email = access.getEmail();
        postFacade.deleteComment(postId, commentId, email);
        return ResponseEntity.ok(Response.success());
    }

    private String getEmailOrNull(Access access) {
        return (access != null) ? access.getEmail() : null;
    }
}
