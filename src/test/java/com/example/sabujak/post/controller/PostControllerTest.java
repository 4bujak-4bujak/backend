package com.example.sabujak.post.controller;

import com.example.sabujak.common.config.TestInitializer;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.post.dto.SaveCommentRequest;
import com.example.sabujak.post.dto.SavePostLikeRequest;
import com.example.sabujak.post.dto.SavePostRequest;
import com.example.sabujak.post.entity.*;
import com.example.sabujak.post.repository.CommentRepository;
import com.example.sabujak.post.repository.PostImageRepository;
import com.example.sabujak.post.repository.PostLikeRepository;
import com.example.sabujak.post.repository.PostRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static com.example.sabujak.common.utils.CommentUtils.createComment;
import static com.example.sabujak.common.utils.CommentUtils.createSaveCommentRequest;
import static com.example.sabujak.common.utils.MemberUtils.createInvaildMember;
import static com.example.sabujak.common.utils.PostImageUtils.createPostImage;
import static com.example.sabujak.common.utils.PostLikeUtils.createPostLike;
import static com.example.sabujak.common.utils.PostLikeUtils.createPostLikeId;
import static com.example.sabujak.common.utils.PostUtils.*;
import static com.example.sabujak.post.entity.Category.OWNER;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.snippet.Attributes.key;

@DisplayName("PostController_테스트")
public class PostControllerTest extends TestInitializer {

//    @Autowired
//    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

//    @Autowired
//    private PostImageRepository postImageRepository;
//
//    @Autowired
//    private PostLikeRepository postLikeRepository;
//
//    @Autowired
//    CommentRepository commentRepository;

//    @Test
//    @DisplayName("전체_게시글_조회_성공")
//    void all_post_get_success() {
//        for(int i = 0; i < 10; i ++) {
//            Post post = savePost(member);
//            savePostImages(post);
//        }
//        Long cursorId = 6L;
//        String url = "/posts?category={category}&cursorId={cursorId}";
//
//        // given
//        RequestSpecification specification = RestAssured
//                .given(spec).log().all()
//                .contentType(APPLICATION_JSON_VALUE);
//
//        // when
//        Response response = specification
//                .header("Authorization", "Bearer " + accessToken)
//                .filter(document(
//                        DEFAULT_IDENTIFIER,
//                        queryParameters(
//                                parameterWithName("category").description("게시글 카테고리"),
//                                parameterWithName("cursorId").description("커서 아이디")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").type(STRING).description("응답 상태"),
//                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),
//
//                                fieldWithPath("data.content[0].postId").type(NUMBER).description("게시글 아이디"),
//                                fieldWithPath("data.content[0].category").type(STRING).description("게시글 카테고리"),
//                                fieldWithPath("data.content[0].tag").type(STRING).description("게시글 태그"),
//                                fieldWithPath("data.content[0].title").type(STRING).description("게시글 제목"),
//                                fieldWithPath("data.content[0].content").type(STRING).description("게시글 내용"),
//                                fieldWithPath("data.content[0].createdDate").type(STRING).description("게시글 생성일"),
//                                fieldWithPath("data.content[0].viewCount").type(NUMBER).description("조회수"),
//                                fieldWithPath("data.content[0].likeCount").type(NUMBER).description("좋아요 수"),
//                                fieldWithPath("data.content[0].commentCount").type(NUMBER).description("댓글 수"),
//                                fieldWithPath("data.content[0].images[]").type(ARRAY).description("게시글 이미지 목록"),
//                                fieldWithPath("data.content[0].writer.profile").type(STRING).description("작성자 프로필 이미지 경로"),
//                                fieldWithPath("data.content[0].writer.job").type(STRING).description("작성자 관심 직무"),
//                                fieldWithPath("data.content[0].writer.nickname").type(STRING).description("작성자 닉네임"),
//                                fieldWithPath("data.content[0].isWriter").type(BOOLEAN).description("작성자 여부"),
//                                fieldWithPath("data.content[0].isLiked").type(BOOLEAN).description("좋아요 여부"),
//
//                                fieldWithPath("data.hasNext").type(BOOLEAN).description("다음 페이지 존재 여부"),
//
//                                fieldWithPath("message").type(NULL).description("응답 메시지")
//                        )
//                ))
//                .when()
//                .get(url, OWNER, cursorId);
//
//        // then
//        response
//                .then().log().all()
//                .assertThat().statusCode(is(OK.value()));
//    }
//
//    @Test
//    @DisplayName("게시글_조회_성공")
//    void post_get_success() {
//        Post post = savePost(member);
//        savePostImages(post);
//        Long postId = post.getId();
//        String url = "/posts/{postId}";
//
//        // given
//        RequestSpecification specification = RestAssured
//                .given(spec).log().all()
//                .contentType(APPLICATION_JSON_VALUE);
//
//        // when
//        Response response = specification
//                .header("Authorization", "Bearer " + accessToken)
//                .filter(document(
//                        DEFAULT_IDENTIFIER,
//                        pathParameters(
//                                parameterWithName("postId").description("게시글 아이디")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").type(STRING).description("응답 상태"),
//                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),
//
//                                fieldWithPath("data.postId").type(NUMBER).description("게시글 아이디"),
//                                fieldWithPath("data.category").type(STRING).description("게시글 카테고리"),
//                                fieldWithPath("data.tag").type(STRING).description("게시글 태그"),
//                                fieldWithPath("data.title").type(STRING).description("게시글 제목"),
//                                fieldWithPath("data.content").type(STRING).description("게시글 내용"),
//                                fieldWithPath("data.createdDate").type(STRING).description("게시글 생성일"),
//                                fieldWithPath("data.viewCount").type(NUMBER).description("조회수"),
//                                fieldWithPath("data.likeCount").type(NUMBER).description("좋아요 수"),
//                                fieldWithPath("data.commentCount").type(NUMBER).description("댓글 수"),
//                                fieldWithPath("data.images[]").type(ARRAY).description("게시글 이미지 목록"),
//                                fieldWithPath("data.writer.profile").type(STRING).description("작성자 프로필 이미지 경로"),
//                                fieldWithPath("data.writer.job").type(STRING).description("작성자 관심 직무"),
//                                fieldWithPath("data.writer.nickname").type(STRING).description("작성자 닉네임"),
//                                fieldWithPath("data.isWriter").type(BOOLEAN).description("작성자 여부"),
//                                fieldWithPath("data.isLiked").type(BOOLEAN).description("좋아요 여부"),
//
//                                fieldWithPath("message").type(NULL).description("응답 메시지")
//                        )
//                ))
//                .when()
//                .get(url, postId);
//
//        // then
//        response
//                .then().log().all()
//                .assertThat().statusCode(is(OK.value()));
//    }
//
//    @Test
//    @DisplayName("게시글_조회_실패: 존재하지_않는_게시글")
//    void post_get_fail() {
//        savePost(member);
//        Long postId = 2L;
//        String url = "/posts/{postId}";
//
//        // given
//        RequestSpecification specification = RestAssured
//                .given(spec).log().all()
//                .contentType(APPLICATION_JSON_VALUE);
//
//        // when
//        Response response = specification
//                .filter(document(
//                        DEFAULT_IDENTIFIER,
//                        pathParameters(
//                                parameterWithName("postId").description("게시글 아이디")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").type(STRING).description("응답 상태"),
//                                fieldWithPath("errorCode").type(STRING).description("에러 코드"),
//                                fieldWithPath("data").type(NULL).description("응답 데이터"),
//                                fieldWithPath("message").type(STRING).description("응답 메시지")
//                        )
//                ))
//                .when()
//                .get(url, postId);
//
//        // then
//        response
//                .then().log().all()
//                .assertThat().statusCode(is(BAD_REQUEST.value()));
//    }
//
//    @Test
//    @DisplayName("게시글_등록_성공")
//    void post_register_success() throws IOException {
//        SavePostRequest request = createSavePostRequest();
//        String url = "/posts";
//
//        // given
//        RequestSpecification specification = RestAssured
//                .given(spec).log().all()
//                .contentType(MULTIPART_FORM_DATA_VALUE)
//                .multiPart("savePostRequest", request, APPLICATION_JSON_VALUE);
//
//        // when
//        Response response = specification
//                .header("Authorization", "Bearer " + accessToken)
//                .filter(document(
//                        DEFAULT_IDENTIFIER,
//                        requestParts(
//                                partWithName("savePostRequest").description("게시글 저장 요청 데이터")
//                                        .attributes(
//                                                key("category").value("게시글 카테고리"),
//                                                key("tag").value("게시글 태그"),
//                                                key("title").value("게시글 제목"),
//                                                key("content").value("게시글 내용")
//                                        )
//                        ),
//                        responseFields(
//                                fieldWithPath("status").type(STRING).description("응답 상태"),
//                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),
//                                fieldWithPath("data.postId").type(NUMBER).description("게시글 아이디"),
//                                fieldWithPath("message").type(NULL).description("응답 메시지")
//                        )
//                ))
//                .when()
//                .post(url);
//
//        // then
//        response
//                .then().log().all()
//                .assertThat().statusCode(is(OK.value()));
//    }
//
//    @Test
//    @DisplayName("게시글_삭제_성공")
//    void post_delete_success() {
//        Post post = savePost(member);
//        Long postId = post.getId();
//        String url = "/posts/{postId}";
//
//        // given
//        RequestSpecification specification = RestAssured
//                .given(spec).log().all()
//                .contentType(APPLICATION_JSON_VALUE);
//
//        // when
//        Response response = specification
//                .header("Authorization", "Bearer " + accessToken)
//                .filter(document(
//                        DEFAULT_IDENTIFIER,
//                        pathParameters(
//                                parameterWithName("postId").description("게시글 아이디")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").type(STRING).description("응답 상태"),
//                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),
//                                fieldWithPath("data").type(NULL).description("응답 데이터"),
//                                fieldWithPath("message").type(NULL).description("응답 메시지")
//                        )
//                ))
//                .when()
//                .delete(url, postId);
//
//        // then
//        response
//                .then().log().all()
//                .assertThat().statusCode(is(OK.value()));
//    }
//
//    @Test
//    @DisplayName("게시글_삭제_실패: 권한이_존재하지_않음")
//    void post_delete_fail() {
//        Post post = savePost(saveInvaildMember());
//        Long postId = post.getId();
//        String url = "/posts/{postId}";
//
//        // given
//        RequestSpecification specification = RestAssured
//                .given(spec).log().all()
//                .contentType(APPLICATION_JSON_VALUE);
//
//        // when
//        Response response = specification
//                .header("Authorization", "Bearer " + accessToken)
//                .filter(document(
//                        DEFAULT_IDENTIFIER,
//                        pathParameters(
//                                parameterWithName("postId").description("게시글 아이디")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").type(STRING).description("응답 상태"),
//                                fieldWithPath("errorCode").type(STRING).description("에러 코드"),
//                                fieldWithPath("data").type(NULL).description("응답 데이터"),
//                                fieldWithPath("message").type(STRING).description("응답 메시지")
//                        )
//                ))
//                .when()
//                .delete(url, postId);
//
//        // then
//        response
//                .then().log().all()
//                .assertThat().statusCode(is(BAD_REQUEST.value()));
//    }
//
//    @Test
//    @DisplayName("게시글_좋아요_등록_성공")
//    void post_like_register_success() {
//        Post post = savePost(member);
//        Long postId = post.getId();
//        SavePostLikeRequest request = new SavePostLikeRequest(postId);
//        String url = "/posts/like";
//
//        // given
//        RequestSpecification specification = RestAssured
//                .given(spec).log().all()
//                .contentType(APPLICATION_JSON_VALUE)
//                .body(request);
//
//        // when
//        Response response = specification
//                .header("Authorization", "Bearer " + accessToken)
//                .filter(document(
//                        DEFAULT_IDENTIFIER,
//                        requestFields(
//                                fieldWithPath("postId").type(NUMBER).description("게시글 아이디")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").type(STRING).description("응답 상태"),
//                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),
//                                fieldWithPath("data").type(NULL).description("응답 데이터"),
//                                fieldWithPath("message").type(NULL).description("응답 메시지")
//                        )
//                ))
//                .when()
//                .post(url);
//
//        // then
//        response
//                .then().log().all()
//                .assertThat().statusCode(is(OK.value()));
//    }
//
//    @Test
//    @DisplayName("게시글_좋아요_삭제_성공")
//    void post_like_delete_success() {
//        Post post = savePost(member);
//        Long postId = post.getId();
//        savePostLike(postId, member.getMemberEmail());
//        String url = "/posts/{postId}/like";
//
//        // given
//        RequestSpecification specification = RestAssured
//                .given(spec).log().all()
//                .contentType(APPLICATION_JSON_VALUE);
//
//        // when
//        Response response = specification
//                .header("Authorization", "Bearer " + accessToken)
//                .filter(document(
//                        DEFAULT_IDENTIFIER,
//                        pathParameters(
//                                parameterWithName("postId").description("게시글 아이디")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").type(STRING).description("응답 상태"),
//                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),
//                                fieldWithPath("data").type(NULL).description("응답 데이터"),
//                                fieldWithPath("message").type(NULL).description("응답 메시지")
//                        )
//                ))
//                .when()
//                .delete(url, postId);
//
//        // then
//        response
//                .then().log().all()
//                .assertThat().statusCode(is(OK.value()));
//    }
//
//    @Test
//    @DisplayName("게시글_좋아요_삭제_실패: 존재하지_않는_관심_글")
//    void post_like_delete_fail() {
//        Post post = savePost(member);
//        savePostLike(post.getId(), member.getMemberEmail());
//        Long postId = 2L;
//        String url = "/posts/{postId}/like";
//
//        // given
//        RequestSpecification specification = RestAssured
//                .given(spec).log().all()
//                .contentType(APPLICATION_JSON_VALUE);
//
//        // when
//        Response response = specification
//                .header("Authorization", "Bearer " + accessToken)
//                .filter(document(
//                        DEFAULT_IDENTIFIER,
//                        pathParameters(
//                                parameterWithName("postId").description("게시글 아이디")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").type(STRING).description("응답 상태"),
//                                fieldWithPath("errorCode").type(STRING).description("에러 코드"),
//                                fieldWithPath("data").type(NULL).description("응답 데이터"),
//                                fieldWithPath("message").type(STRING).description("응답 메시지")
//                        )
//                ))
//                .when()
//                .delete(url, postId);
//
//        // then
//        response
//                .then().log().all()
//                .assertThat().statusCode(is(BAD_REQUEST.value()));
//    }
//
//    @Test
//    @DisplayName("전체_댓글_조회_성공")
//    void all_comment_get_success() {
//        Post post = savePost(member);
//        for(int i = 0; i < 10; i ++) {
//            saveComment(post, member);
//        }
//        Long postId = post.getId();
//        Long cursorId = 11L;
//        String url = "/posts/{postId}/comments?cursorId={cursorId}";
//
//        // given
//        RequestSpecification specification = RestAssured
//                .given(spec).log().all()
//                .contentType(APPLICATION_JSON_VALUE);
//
//        // when
//        Response response = specification
//                .header("Authorization", "Bearer " + accessToken)
//                .filter(document(
//                        DEFAULT_IDENTIFIER,
//                        queryParameters(
//                                parameterWithName("cursorId").description("커서 아이디")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").type(STRING).description("응답 상태"),
//                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),
//
//                                fieldWithPath("data.content[0].commentId").type(NUMBER).description("댓글 아이디"),
//                                fieldWithPath("data.content[0].content").type(STRING).description("댓글 내용"),
//                                fieldWithPath("data.content[0].createdDate").type(STRING).description("댓글 생성일"),
//                                fieldWithPath("data.content[0].writer.profile").type(STRING).description("작성자 프로필 이미지"),
//                                fieldWithPath("data.content[0].writer.job").type(STRING).description("작성자 관심 직무"),
//                                fieldWithPath("data.content[0].writer.nickname").type(STRING).description("작성자 닉네임"),
//                                fieldWithPath("data.content[0].isWriter").type(BOOLEAN).description("작성자 여부"),
//                                fieldWithPath("data.hasNext").type(BOOLEAN).description("다음 페이지 존재 여부"),
//
//                                fieldWithPath("message").type(NULL).description("응답 메시지")
//                        )
//                ))
//                .when()
//                .get(url, postId, cursorId);
//
//        // then
//        response
//                .then().log().all()
//                .assertThat().statusCode(is(OK.value()));
//    }
//
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
                        pathParameters(
                                parameterWithName("postId").description("게시글 아이디")
                        ),
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
//
//    @Test
//    @DisplayName("댓글_삭제_성공")
//    void comment_delete_success() {
//        Post post = savePost(member);
//        Long postId = post.getId();
//        Comment comment = saveComment(post, member);
//        Long commentId = comment.getId();
//        String url = "/posts/{postId}/comments/{commentId}";
//
//        // given
//        RequestSpecification specification = RestAssured
//                .given(spec).log().all()
//                .contentType(APPLICATION_JSON_VALUE);
//
//        // when
//        Response response = specification
//                .header("Authorization", "Bearer " + accessToken)
//                .filter(document(
//                        DEFAULT_IDENTIFIER,
//                        pathParameters(
//                                parameterWithName("postId").description("게시글 아이디"),
//                                parameterWithName("commentId").description("댓글 아이디")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").type(STRING).description("응답 상태"),
//                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),
//                                fieldWithPath("data").type(NULL).description("응답 데이터"),
//                                fieldWithPath("message").type(NULL).description("응답 메시지")
//                        )
//                ))
//                .when()
//                .delete(url, postId, commentId);
//
//        // then
//        response
//                .then().log().all()
//                .assertThat().statusCode(is(OK.value()));
//    }
//
//    @Test
//    @DisplayName("댓글_삭제_실패: 권한이_존재하지_않음")
//    void comment_delete_fail() {
//        Member member = saveInvaildMember();
//        Post post = savePost(member);
//        Long postId = post.getId();
//        Comment comment = saveComment(post, member);
//        Long commentId = comment.getId();
//        String url = "/posts/{postId}/comments/{commentId}";
//
//        // given
//        RequestSpecification specification = RestAssured
//                .given(spec).log().all()
//                .contentType(APPLICATION_JSON_VALUE);
//
//        // when
//        Response response = specification
//                .header("Authorization", "Bearer " + accessToken)
//                .filter(document(
//                        DEFAULT_IDENTIFIER,
//                        pathParameters(
//                                parameterWithName("postId").description("게시글 아이디"),
//                                parameterWithName("commentId").description("댓글 아이디")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").type(STRING).description("응답 상태"),
//                                fieldWithPath("errorCode").type(STRING).description("에러 코드"),
//                                fieldWithPath("data").type(NULL).description("응답 데이터"),
//                                fieldWithPath("message").type(STRING).description("응답 메시지")
//                        )
//                ))
//                .when()
//                .delete(url, postId, commentId);
//
//        // then
//        response
//                .then().log().all()
//                .assertThat().statusCode(is(BAD_REQUEST.value()));
//    }
//
//    private Member saveInvaildMember() {
//        Member member = createInvaildMember();
//        return memberRepository.save(member);
//    }
//
    private Post savePost(Member member) {
        Post post = createPost();
        post.setMember(member);
        return postRepository.save(post);
    }
//
//    private void savePostImages(Post post) {
//        for(int i = 0; i < 10; i ++) {
//            PostImage postImage = createPostImage(post);
//            postImage.setPost(post);
//            postImageRepository.save(postImage);
//        }
//    }
//
//    private void savePostLike(Long postId, String memberEmail) {
//        PostLikeId postLikeId = createPostLikeId(postId, memberEmail);
//        PostLike postLike = createPostLike(postLikeId);
//        postLikeRepository.save(postLike);
//    }
//
//    private Comment saveComment(Post post, Member member) {
//        Comment comment = createComment();
//        comment.setMember(member);
//        comment.setPost(post);
//        return commentRepository.save(comment);
//    }
}
