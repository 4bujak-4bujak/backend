package com.example.sabujak.post.dto;

import com.example.sabujak.post.entity.Tag;

public record PostSaveRequest(
        Tag tag,
        String title,
        String content
) {
}
