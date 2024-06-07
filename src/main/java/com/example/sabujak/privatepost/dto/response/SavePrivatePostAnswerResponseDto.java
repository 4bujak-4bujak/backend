package com.example.sabujak.privatepost.dto.response;

import com.example.sabujak.privatepost.entity.PrivatePostAnswer;

public record SavePrivatePostAnswerResponseDto(Long id) {
    public static SavePrivatePostAnswerResponseDto of(PrivatePostAnswer privatePostAnswer) {
        return new SavePrivatePostAnswerResponseDto(privatePostAnswer.getId());
    }
}
