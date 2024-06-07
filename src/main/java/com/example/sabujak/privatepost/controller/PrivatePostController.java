package com.example.sabujak.privatepost.controller;

import com.example.sabujak.common.response.Response;
import com.example.sabujak.privatepost.dto.request.SavePrivatePostAnswerRequestDto;
import com.example.sabujak.privatepost.dto.request.SavePrivatePostRequestDto;
import com.example.sabujak.privatepost.dto.response.*;
import com.example.sabujak.privatepost.repository.PrivatePostAnswerRepository;
import com.example.sabujak.privatepost.service.PrivatePostAnswerService;
import com.example.sabujak.privatepost.service.PrivatePostService;
import com.example.sabujak.security.dto.request.AuthRequestDto.Access;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.sabujak.post.constants.PaginationConstants.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Tag(name = "privatePosts", description = "1:1 문의 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/privatePosts")
public class PrivatePostController {

    private final PrivatePostService privatePostService;
    private final PrivatePostAnswerService privatePostAnswerService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작성 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "작성 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "일대일 문의 전체 조회", description = "회원의 일대일 문의 전체 조회")
    @Parameter(name = "cursorId", description = "id에 해당하는 글의 다음부터 조회(비어 있을시 최신글 부터 조회)")
    @GetMapping
    public ResponseEntity<Response<PrivatePostResponsesDto>> getPrivatePosts(
            @RequestParam(required = false) Long cursorId,
            @PageableDefault(
                    page = DEFAULT_PAGE,
                    size = DEFAULT_POST_PAGE_SIZE,
                    sort = DEFAULT_SORT_FIELD,
                    direction = DESC
            ) Pageable pageable,
            @AuthenticationPrincipal Access access
    ){
        String email = getEmailOrNull(access);

        return ResponseEntity.ok(Response.success(PrivatePostResponsesDto.of(privatePostService.getPrivatePosts(pageable, cursorId, email))));
    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "문의 단건 조회", description = "회원의 일대일 문의글 단건 조회")
    @GetMapping("/{privatePostId}")
    public ResponseEntity<Response<PrivatePostResponseDto>> getPrivatePost(@PathVariable Long privatePostId, @AuthenticationPrincipal Access access) {
        String email = getEmailOrNull(access);
        return ResponseEntity.ok(Response.success(privatePostService.getPrivatePost(privatePostId, email)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작성 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "작성 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "일대일 문의 글 작성", description = "일대일 문의 글 작성")
    @PostMapping
    public ResponseEntity<Response<SavePrivatePostResponseDto>> savePrivatePost(
            @RequestBody SavePrivatePostRequestDto savePrivatePostRequestDto,
            @AuthenticationPrincipal Access access
    ){
        String email = getEmailOrNull(access);

        SavePrivatePostResponseDto savePrivatePostResponseDto = privatePostService.savePrivatePost(savePrivatePostRequestDto, email);

        return ResponseEntity.ok(Response.success(savePrivatePostResponseDto));
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "삭제 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "일대일 문의 글 삭제", description = "일대일 문의 글 삭제")
    @DeleteMapping("/{privatePostId}")
    public ResponseEntity<Response<Void>> deletePrivatePost(
            @PathVariable Long privatePostId,
            @AuthenticationPrincipal Access access
    ) {
        String email = getEmailOrNull(access);
        privatePostService.deletePrivatePost(privatePostId, email);
        return ResponseEntity.ok(Response.success());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작성 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "작성 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "일대일 문의 글 답변 작성", description = "일대일 문의 글 답변 작성")
    @PostMapping("/{privatePostId}/answer")
    public ResponseEntity<Response<SavePrivatePostAnswerResponseDto>> savePrivatePostAnswer(
            @PathVariable Long privatePostId,
            @RequestBody SavePrivatePostAnswerRequestDto savePrivatePostAnswerRequestDto,
            @AuthenticationPrincipal Access access

    ) {
        SavePrivatePostAnswerResponseDto savePrivatePostAnswerResponseDto = privatePostAnswerService.savePrivatePostAnswer(savePrivatePostAnswerRequestDto, privatePostId);
        return ResponseEntity.ok(Response.success(savePrivatePostAnswerResponseDto));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작성 성공", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "작성 실패", content = @Content(schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "일대일 문의 글 답변 조회", description = "일대일 문의 글 답변 조회")
    @GetMapping("/{privatePostId}/answer")
    public ResponseEntity<Response<PrivatePostAnswerResponseDto>> getPrivatePostAnswer(
            @PathVariable Long privatePostId,
            @AuthenticationPrincipal Access access
    ){
        PrivatePostAnswerResponseDto privatePostAnswerResponseDto = privatePostAnswerService.getPrivatePostAnswer(privatePostId);
        return ResponseEntity.ok(Response.success(privatePostAnswerResponseDto));
    }


    private String getEmailOrNull(Access access) {
        return (access != null) ? access.getEmail() : null;
    }
}
