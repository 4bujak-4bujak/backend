package com.example.sabujak.privatepost.dto.response;

import org.springframework.data.domain.Slice;

import java.util.List;

public record PrivatePostResponsesDto(List<PrivatePostResponseDto> privatePostList, boolean hasNext) {
    public static PrivatePostResponsesDto of(Slice<PrivatePostResponseDto> slice) {
        return new PrivatePostResponsesDto(slice.getContent(), slice.hasNext());
    }
}
