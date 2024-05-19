package com.example.sabujak.post.controller;

import com.example.sabujak.common.config.TestInitializer;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.post.dto.SavePostLikeRequest;
import com.example.sabujak.post.dto.SavePostRequest;
import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.entity.PostLike;
import com.example.sabujak.post.entity.PostLikeId;
import com.example.sabujak.post.repository.PostLikeRepository;
import com.example.sabujak.post.repository.PostRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.sabujak.common.utils.MemberUtils.createInvaildMember;
import static com.example.sabujak.common.utils.PostLikesUtils.createPostLike;
import static com.example.sabujak.common.utils.PostLikesUtils.createPostLikeId;
import static com.example.sabujak.common.utils.PostUtils.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

@DisplayName("PostController_테스트")
public class PostControllerTest extends TestInitializer {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Test
    @DisplayName("게시글_조회_성공")
    void post_get_success() {
        Long postId = savePost(member).getId();
        String url = "/posts/{postId}";

        // given
        RequestSpecification specification = RestAssured
                .given(spec).log().all()
                .contentType(APPLICATION_JSON_VALUE);

        // when
        Response response = specification
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        DEFAULT_IDENTIFIER,
                        pathParameters(
                                parameterWithName("postId").description("게시글 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),

                                fieldWithPath("data.category").type(STRING).description("게시글 카테고리"),
                                fieldWithPath("data.tag").type(STRING).description("게시글 태그"),
                                fieldWithPath("data.title").type(STRING).description("게시글 제목"),
                                fieldWithPath("data.content").type(STRING).description("게시글 내용"),
                                fieldWithPath("data.createdDate").type(STRING).description("게시글 생성일"),
                                fieldWithPath("data.viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("data.likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("data.commentCount").type(NUMBER).description("댓글 수"),
                                fieldWithPath("data.job").type(STRING).description("작성자 관심 직무"),
                                fieldWithPath("data.nickname").type(STRING).description("작성자 닉네임"),
                                fieldWithPath("data.isWriter").type(BOOLEAN).description("작성자 여부"),
                                fieldWithPath("data.isLiked").type(BOOLEAN).description("좋아요 여부"),

                                fieldWithPath("message").type(NULL).description("응답 메시지")
                        )
                ))
                .when()
                .get(url, postId);

        // then
        response
                .then().log().all()
                .assertThat().statusCode(is(OK.value()));
    }

    @Test
    @DisplayName("게시글_조회_실패: 존재하지_않는_게시글")
    void post_get_fail() {
        savePost(member);
        Long postId = 2L;
        String url = "/posts/{postId}";

        // given
        RequestSpecification specification = RestAssured
                .given(spec).log().all()
                .contentType(APPLICATION_JSON_VALUE);

        // when
        Response response = specification
                .filter(document(
                        DEFAULT_IDENTIFIER,
                        pathParameters(
                                parameterWithName("postId").description("게시글 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("errorCode").type(STRING).description("에러 코드"),
                                fieldWithPath("data").type(NULL).description("응답 데이터"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ))
                .when()
                .get(url, postId);

        // then
        response
                .then().log().all()
                .assertThat().statusCode(is(BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("게시글_등록_성공")
    void post_register_success() {
        SavePostRequest request = createSavePostRequest();
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
    @DisplayName("게시글_삭제_성공")
    void post_delete_success() {
        Post post = savePost(member);
        Long postId = post.getId();
        String url = "/posts/{postId}";

        // given
        RequestSpecification specification = RestAssured
                .given(spec).log().all()
                .contentType(APPLICATION_JSON_VALUE);

        // when
        Response response = specification
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        DEFAULT_IDENTIFIER,
                        pathParameters(
                                parameterWithName("postId").description("게시글 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),
                                fieldWithPath("data").type(NULL).description("응답 데이터"),
                                fieldWithPath("message").type(NULL).description("응답 메시지")
                        )
                ))
                .when()
                .delete(url, postId);

        // then
        response
                .then().log().all()
                .assertThat().statusCode(is(OK.value()));
    }

    @Test
    @DisplayName("게시글_삭제_실패: 권한이_존재하지_않음")
    void post_delete_fail() {
        Post post = savePost(saveInvaildMember());
        Long postId = post.getId();
        String url = "/posts/{postId}";

        // given
        RequestSpecification specification = RestAssured
                .given(spec).log().all()
                .contentType(APPLICATION_JSON_VALUE);

        // when
        Response response = specification
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        DEFAULT_IDENTIFIER,
                        pathParameters(
                                parameterWithName("postId").description("게시글 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("errorCode").type(STRING).description("에러 코드"),
                                fieldWithPath("data").type(NULL).description("응답 데이터"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ))
                .when()
                .delete(url, postId);

        // then
        response
                .then().log().all()
                .assertThat().statusCode(is(BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("게시글_좋아요_등록_성공")
    void post_like_register_success() {
        Post post = savePost(member);
        Long postId = post.getId();
        SavePostLikeRequest request = new SavePostLikeRequest(postId);
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
                                fieldWithPath("data").type(NULL).description("응답 데이터"),
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
    @DisplayName("게시글_좋아요_삭제_성공")
    void post_like_delete_success() {
        Post post = savePost(member);
        Long postId = post.getId();
        savePostLike(postId, member.getMemberEmail());
        String url = "/posts/{postId}/like";

        // given
        RequestSpecification specification = RestAssured
                .given(spec).log().all()
                .contentType(APPLICATION_JSON_VALUE);

        // when
        Response response = specification
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        DEFAULT_IDENTIFIER,
                        pathParameters(
                                parameterWithName("postId").description("게시글 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),
                                fieldWithPath("data").type(NULL).description("응답 데이터"),
                                fieldWithPath("message").type(NULL).description("응답 메시지")
                        )
                ))
                .when()
                .delete(url, postId);

        // then
        response
                .then().log().all()
                .assertThat().statusCode(is(OK.value()));
    }

    @Test
    @DisplayName("게시글_좋아요_삭제_실패: 존재하지_않는_관심_글")
    void post_like_delete_fail() {
        Post post = savePost(member);
        savePostLike(post.getId(), member.getMemberEmail());
        Long postId = 2L;
        String url = "/posts/{postId}/like";

        // given
        RequestSpecification specification = RestAssured
                .given(spec).log().all()
                .contentType(APPLICATION_JSON_VALUE);

        // when
        Response response = specification
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        DEFAULT_IDENTIFIER,
                        pathParameters(
                                parameterWithName("postId").description("게시글 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("errorCode").type(STRING).description("에러 코드"),
                                fieldWithPath("data").type(NULL).description("응답 데이터"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ))
                .when()
                .delete(url, postId);

        // then
        response
                .then().log().all()
                .assertThat().statusCode(is(BAD_REQUEST.value()));
    }

    private Member saveInvaildMember() {
        Member member = createInvaildMember();
        return memberRepository.save(member);
    }

    private Post savePost(Member member) {
        Post post = createPost();
        post.setMember(member);
        return postRepository.save(post);
    }

    private void savePostLike(Long postId, String memberEmail) {
        PostLikeId postLikeId = createPostLikeId(postId, memberEmail);
        PostLike postLike = createPostLike(postLikeId);
        postLikeRepository.save(postLike);
    }
}
