package com.example.sabujak.privatepost.dto.response;

import com.example.sabujak.privatepost.entity.PrivatePost;

import java.time.LocalDate;

public record PrivatePostResponseDto(Long id, String title, String content, LocalDate createdDate) {
    public static PrivatePostResponseDto of(PrivatePost privatePost) {
        return new PrivatePostResponseDto(privatePost.getId(), privatePost.getTitle(), privatePost.getContent(), privatePost.getCreatedDate().toLocalDate());
    }
}