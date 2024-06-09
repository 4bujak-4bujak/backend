package com.example.sabujak.privatepost.dto.response;

import com.example.sabujak.privatepost.entity.PrivatePostAnswer;

public record PrivatePostAnswerResponseDto(Long privatePostAnswerId, String content) {
    public static PrivatePostAnswerResponseDto of(PrivatePostAnswer privatePostAnswer) {
        if (privatePostAnswer == null) return null;
        
        return new PrivatePostAnswerResponseDto(privatePostAnswer.getId(), privatePostAnswer.getContent());
    }
}
