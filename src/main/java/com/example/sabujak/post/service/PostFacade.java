package com.example.sabujak.post.service;

import com.example.sabujak.post.dto.*;
import com.example.sabujak.post.dto.SaveCommentRequest;
import com.example.sabujak.post.dto.SavePostLikeRequest;
import com.example.sabujak.post.dto.SavePostRequest;
import com.example.sabujak.post.entity.*;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.sabujak.fcm.constants.FCMConstants.COMMUNITY_URL_PREFIX;
import static com.example.sabujak.notification.utils.NotificationContent.createCommentContent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostFacade {

    private final MemberService memberService;
    private final PostService postService;
    private final PostImageService postImageService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;

    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public CustomSlice<PostResponse> getPosts(Category category, Long cursorId, Pageable pageable, String viewerEmail) {
        log.info("Getting Posts. Cursor ID: [{}], Viewer Email: [{}]", cursorId, viewerEmail);

        CustomSlice<Post> posts = (category.getParent() != null) ?
                postService.findPostsByCategory(category, cursorId, pageable) :
                postService.findPostsByCategories(category, cursorId, pageable);

        List<PostResponse> postResponses = posts.content().stream()
                .map(post -> createPostResponse(post, viewerEmail))
                .collect(Collectors.toList());

        return new CustomSlice<>(postResponses, posts.hasNext());
    }

    @Transactional
    public PostResponse getPost(Long postId, String viewerEmail) {
        log.info("Getting Post. Post ID: [{}] Viewer Email: [{}]", postId, viewerEmail);

        Post post = postService.findPostWithMemberAndImages(postId);
        if (!postService.isViewed(postId, viewerEmail)) {
            postService.increaseViewCount(post);
            postService.setViewed(postId, viewerEmail);
        }
        log.info("Increased Post View Count. View Count: [{}]", post.getViewCount());

        return createPostResponse(post, viewerEmail);
    }

    @Transactional
    public SavePostResponse savePost(SavePostRequest savePostRequest, MultipartFile[] images, String writerEmail) {
        Member writer = memberService.findMember(writerEmail);
        Post post = savePostRequest.toEntity(writer);
        log.info("Saving Post. Writer Email: [{}] Post: [{}]", writerEmail, post);

        postService.savePost(post);
        if (images == null || images.length == 0) {
            log.info("Saved Post Without Images. Post ID: [{}]", post.getId());
        } else {
            postImageService.saveImages(post, images);
            log.info("Saved Post And Images. Post ID: [{}]", post.getId());
        }

        return SavePostResponse.of(post);
    }

    @Transactional
    public void deletePost(Long postId, String deleterEmail) {
        log.info("Deleting Post. Post ID: [{}]", postId);
        Post post = postService.findPostWithMember(postId);

        Member writer = post.getMember();
        String writerEmail = writer.getMemberEmail();
        log.info("Check Permission. Deleter Email: [{}], Writer Email: [{}]", deleterEmail, writerEmail);
        postService.isWriterOrThrow(deleterEmail, writerEmail);

        postService.deletePost(post);
        log.info("Deleted Post. Post: [{}]", post);
    }

    @Transactional
    public void savePostLike(SavePostLikeRequest savePostLikeRequest, String email) {
        Long postId = savePostLikeRequest.postId();
        log.info("Saving Post Like. Post ID: [{}], Member Email: [{}]", postId, email);

        Post post = postService.findPost(postId);
        postService.increaseLikeCount(post);
        log.info("Increased Post Like Count. Like Count: [{}]", post.getLikeCount());

        PostLikeId postLikeId = postLikeService.createPostLikeId(postId, email);
        PostLike postLike = postLikeService.createPostLike(postLikeId);
        postLikeService.savePostLike(postLike);
        log.info("Saved Post Like. Post Like ID: [{}]", postLikeId);
    }

    @Transactional
    public void deletePostLike(Long postId, String email) {
        log.info("Deleting Post Like. Post ID: [{}], Member Email: [{}]", postId, email);

        Post post = postService.findPost(postId);
        postService.decreaseLikeCount(post);
        log.info("Decreased Post Like Count. Like Count: [{}]", post.getLikeCount());

        PostLikeId postLikeId = postLikeService.createPostLikeId(postId, email);
        PostLike postLike = postLikeService.findPostLikeOrThrow(postLikeId);
        postLikeService.deletePostLike(postLike);
        log.info("Deleted Post Like. Post Like: [{}]", postLike);
    }

    @Transactional(readOnly = true)
    public CustomSlice<CommentResponse> getComments(Long postId, Long cursorId, Pageable pageable, String viewerEmail) {
        log.info("Getting Comments. Post ID: [{}], Cursor ID: [{}], Viewer Email: [{}]", postId, cursorId, viewerEmail);

        CustomSlice<Comment> comments = commentService.findComments(postId, cursorId, pageable);
        List<CommentResponse> commentResponses = comments.content().stream()
                .map(comment -> createCommentResponse(comment, viewerEmail))
                .collect(Collectors.toList());

        return new CustomSlice<>(commentResponses, comments.hasNext());
    }

    @Transactional
    public void saveComment(Long postId, SaveCommentRequest saveCommentRequest, String commenterEmail) {
        log.info("Saving Post Comment. Post ID: [{}], Commenter Email: [{}]", postId, commenterEmail);

        Post post = postService.findPostWithMember(postId);
        postService.increaseCommentCount(post);
        log.info("Increased Post Comment Count. Comment Count: [{}]", post.getCommentCount());

        Member commenter = memberService.findMember(commenterEmail);
        Comment comment = saveCommentRequest.toEntity(commenter, post);
        commentService.saveComment(comment);
        log.info("Saved Comment. Comment ID: [{}]", comment.getId());

        Member writer = post.getMember();
        String writerEmail = writer.getMemberEmail();
        log.info("Checking If Commenter Is Writer. Commenter: [{}], Writer: [{}]", commenterEmail, writerEmail);
        if(!commenterEmail.equals(writerEmail)) {
            log.info("Creating and Publishing Event for Writer Notification.");
            String content = createCommentContent(post.getTitle(), commenter.getMemberNickname());
            String targetUrl = createTargetUrl(post.getId());
            publisher.publishEvent(new SaveCommentEvent(targetUrl, content, commenter.getImage().getImageUrl(), writerEmail, writer));
        }
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, String deleterEmail) {
        log.info("Deleting Comment. Post ID: [{}], Comment ID: [{}]", postId, commentId);
        Comment comment = commentService.findCommentWithMemberAndPost(postId, commentId);

        Post post = comment.getPost();
        postService.decreaseCommentCount(post);
        log.info("Decreased Post Comment Count. Comment Count: [{}]", post.getCommentCount());

        Member writer = comment.getMember();
        String writerEmail = writer.getMemberEmail();
        log.info("Check Permission. Deleter Email: [{}], Writer Email: [{}]", deleterEmail, writerEmail);
        commentService.isWriterOrThrow(deleterEmail, writerEmail);

        commentService.deleteComment(comment);
        log.info("Deleted Comment. Comment: [{}]", comment);
    }

    private PostResponse createPostResponse(Post post, String viewerEmail) {
        Member writer = post.getMember();
        String writerEmail = writer.getMemberEmail();
        log.info("Writer Email: [{}]", writerEmail);

        boolean isAuthenticated = (viewerEmail != null);
        boolean isWriter = isAuthenticated && postService.isWriter(viewerEmail, writerEmail);
        boolean isLiked = isAuthenticated && postLikeService.isLiked(post.getId(), viewerEmail);
        log.info("Status Checked. Is Writer: [{}] Is Liked: [{}]", isWriter, isLiked);

        return PostResponse.of(post, writer, isWriter, isLiked);
    }

    private CommentResponse createCommentResponse(Comment comment, String viewerEmail) {
        Member writer = comment.getMember();
        String writerEmail = writer.getMemberEmail();
        log.info("Writer Email: [{}]", writerEmail);

        boolean isAuthenticated = (viewerEmail != null);
        boolean isWriter = isAuthenticated && commentService.isWriter(viewerEmail, writerEmail);
        log.info("Status Checked. Is Writer: [{}]", isWriter);

        return CommentResponse.of(comment, writer, isWriter);
    }

    private String createTargetUrl(Long id) {
        return COMMUNITY_URL_PREFIX + id;
    }
}
