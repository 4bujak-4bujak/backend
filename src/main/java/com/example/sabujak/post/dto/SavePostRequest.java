package com.example.sabujak.post.dto;

import com.example.sabujak.member.entity.Member;
import com.example.sabujak.post.entity.Category;
import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.entity.Tag;

public record SavePostRequest(
        Category category,
        Tag tag,
        String title,
        String content
) {
    public Post toEntity(Member member) {
        Post post = Post.builder()
                .category(category)
                .tag(tag)
                .title(title)
                .content(content)
                .build();
        post.setMember(member);
        return post;
    }
}
