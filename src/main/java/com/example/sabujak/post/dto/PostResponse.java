package com.example.sabujak.post.dto;

import com.example.sabujak.member.entity.Job;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.post.entity.Category;
import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.entity.Tag;
import java.time.LocalDateTime;

public record PostResponse(
        Category category,
        Tag tag,
        String title,
        String content,
        LocalDateTime createdDate,
        int viewCount,
        int likeCount,
        int commentCount,
        Job job,
        String nickname,
        String profile,
        boolean isWriter,
        boolean isLiked
) {
    public static PostResponse of(Post post, Member member, boolean isWriter, boolean isLiked) {
        return new PostResponse(
                post.getCategory(),
                post.getTag(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedDate(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                member.getMemberJob(),
                member.getMemberNickname(),
                member.getImage().getImageUrl(),
                isWriter,
                isLiked
        );
    }
}
