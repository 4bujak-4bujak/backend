package com.example.sabujak.privatepost.dto.response;

import com.example.sabujak.privatepost.entity.PrivatePost;

import java.time.LocalDate;

public record PrivatePostResponseDto(Long id, String title, String content, String branchName,LocalDate createdDate) {
    public static PrivatePostResponseDto of(PrivatePost privatePost) {
        return new PrivatePostResponseDto(privatePost.getId(), privatePost.getTitle(), privatePost.getContent(), privatePost.getBranch().getBranchName(), privatePost.getCreatedDate().toLocalDate());
    }
}
