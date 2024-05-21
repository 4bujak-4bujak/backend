package com.example.sabujak.post.dto;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.post.entity.Category;
import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.entity.Tag;
import java.time.LocalDateTime;

public record PostResponse(
        Long postId,
        Category category,
        Tag tag,
        String title,
        String content,
        LocalDateTime createdDate,
        int viewCount,
        int likeCount,
        int commentCount,
        WriterResponse writer,
        boolean isWriter,
        boolean isLiked
) {
    public static PostResponse of(Post post, Member member, boolean isWriter, boolean isLiked) {
        WriterResponse writer = WriterResponse.of(member);
        return new PostResponse(
                post.getId(),
                post.getCategory(),
                post.getTag(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedDate(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                writer,
                isWriter,
                isLiked
        );
    }
}
