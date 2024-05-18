package com.example.sabujak.post.controller;

import com.example.sabujak.common.config.TestInitializer;
import com.example.sabujak.post.dto.PostLikeSaveRequest;
import com.example.sabujak.post.dto.PostSaveRequest;
import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.repository.PostRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.sabujak.common.utils.PostUtils.createPost;
import static com.example.sabujak.common.utils.PostUtils.createPostSaveRequest;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

@DisplayName("PostController_테스트")
public class PostControllerTest extends TestInitializer {

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("게시글_등록_성공_테스트")
    void post_register_success() {
        PostSaveRequest request = createPostSaveRequest();
        String url = "/posts";

        // given
        RequestSpecification specification = RestAssured
                .given(spec).log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .body(request);

        // when
        Response response = specification
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        DEFAULT_IDENTIFIER,
                        requestFields(
                                fieldWithPath("category").type(STRING).description("게시글 카테고리"),
                                fieldWithPath("tag").type(STRING).description("게시글 태그"),
                                fieldWithPath("title").type(STRING).description("게시글 제목"),
                                fieldWithPath("content").type(STRING).description("게시글 내용")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),
                                fieldWithPath("data.postId").type(NUMBER).description("게시글 아이디"),
                                fieldWithPath("message").type(NULL).description("응답 메시지")
                        )
                ))
                .when()
                .post(url);

        // then
        response
                .then().log().all()
                .assertThat().statusCode(is(OK.value()));
    }

    @Test
    @DisplayName("게시글_좋아요_등록_성공_테스트")
    void post_like_register_success() {
        PostLikeSaveRequest request = new PostLikeSaveRequest(savePost().getId());
        String url = "/posts/like";

        // given
        RequestSpecification specification = RestAssured
                .given(spec).log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .body(request);

        // when
        Response response = specification
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        DEFAULT_IDENTIFIER,
                        requestFields(
                                fieldWithPath("postId").type(NUMBER).description("게시글 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),
                                fieldWithPath("data").type(NULL).description("데이터"),
                                fieldWithPath("message").type(NULL).description("응답 메시지")
                        )
                ))
                .when()
                .post(url);

        // then
        response
                .then().log().all()
                .assertThat().statusCode(is(OK.value()));
    }

    private Post savePost() {
        return postRepository.save(createPost());
    }
}
