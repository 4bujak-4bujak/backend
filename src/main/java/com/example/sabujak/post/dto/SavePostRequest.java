package com.example.sabujak.post.dto;

import com.example.sabujak.post.entity.Category;
import com.example.sabujak.post.entity.Tag;

public record SavePostRequest(
        Category category,
        Tag tag,
        String title,
        String content
) {
}
