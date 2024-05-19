package com.example.sabujak.comment;

import com.example.sabujak.comment.dto.SaveCommentRequest;
import com.example.sabujak.common.config.TestInitializer;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.repository.PostRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.sabujak.common.utils.CommentUtils.createSaveCommentRequest;
import static com.example.sabujak.common.utils.PostUtils.createPost;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

@DisplayName("CommentController_테스트")
public class CommentControllerTest extends TestInitializer {

    @Autowired
    PostRepository postRepository;

    @Test
    @DisplayName("댓글_등록_성공")
    void comment_register_success() {
        Post post = savePost(member);
        Long postId = post.getId();
        SaveCommentRequest request = createSaveCommentRequest();
        String url = "/posts/{postId}/comments";

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
                                fieldWithPath("content").type(STRING).description("댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),
                                fieldWithPath("data").type(NULL).description("응답 데이터"),
                                fieldWithPath("message").type(NULL).description("응답 메시지")
                        )
                ))
                .when()
                .post(url, postId);

        // then
        response
                .then().log().all()
                .assertThat().statusCode(is(OK.value()));
    }

    private Post savePost(Member member) {
        Post post = createPost();
        post.setMember(member);
        return postRepository.save(post);
    }
}
