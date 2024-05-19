package com.example.sabujak.comment;

import com.example.sabujak.comment.dto.SaveCommentRequest;
import com.example.sabujak.comment.entity.Comment;
import com.example.sabujak.comment.repository.CommentRepository;
import com.example.sabujak.common.config.TestInitializer;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.repository.PostRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.sabujak.common.utils.CommentUtils.createComment;
import static com.example.sabujak.common.utils.CommentUtils.createSaveCommentRequest;
import static com.example.sabujak.common.utils.MemberUtils.createInvaildMember;
import static com.example.sabujak.common.utils.PostUtils.createPost;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

@DisplayName("CommentController_테스트")
public class CommentControllerTest extends TestInitializer {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

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

    @Test
    @DisplayName("댓글_삭제_성공")
    void comment_delete_success() {
        Post post = savePost(member);
        Long postId = post.getId();
        Comment comment = saveComment(post, member);
        Long commentId = comment.getId();
        String url = "/posts/{postId}/comments/{commentId}";

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
                                parameterWithName("postId").description("게시글 아이디"),
                                parameterWithName("commentId").description("댓글 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("errorCode").type(NULL).description("에러 코드"),
                                fieldWithPath("data").type(NULL).description("응답 데이터"),
                                fieldWithPath("message").type(NULL).description("응답 메시지")
                        )
                ))
                .when()
                .delete(url, postId, commentId);

        // then
        response
                .then().log().all()
                .assertThat().statusCode(is(OK.value()));
    }

    @Test
    @DisplayName("댓글_삭제_실패: 권한이_존재하지_않음")
    void comment_delete_fail() {
        Member member = saveInvaildMember();
        Post post = savePost(member);
        Long postId = post.getId();
        Comment comment = saveComment(post, member);
        Long commentId = comment.getId();
        String url = "/posts/{postId}/comments/{commentId}";

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
                                parameterWithName("postId").description("게시글 아이디"),
                                parameterWithName("commentId").description("댓글 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("errorCode").type(STRING).description("에러 코드"),
                                fieldWithPath("data").type(NULL).description("응답 데이터"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ))
                .when()
                .delete(url, postId, commentId);

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

    private Comment saveComment(Post post, Member member) {
        Comment comment = createComment();
        comment.setMember(member);
        comment.setPost(post);
        return commentRepository.save(comment);
    }
}
