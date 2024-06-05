package com.example.sabujak.privatepost.dto.response;

import com.example.sabujak.privatepost.entity.PrivatePost;

public record SavePrivatePostResponseDto(Long id) {
    public static SavePrivatePostResponseDto of(PrivatePost privatePost) {
        return new SavePrivatePostResponseDto(privatePost.getId());
    }
}
