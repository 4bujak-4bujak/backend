package com.example.sabujak.privatepost.dto.request;

import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.privatepost.entity.PrivatePost;
import com.example.sabujak.privatepost.entity.PrivatePostAnswer;

public record SavePrivatePostAnswerRequestDto(String content) {

    public PrivatePostAnswer toEntity(PrivatePost privatePost) {
        PrivatePostAnswer privatePostAnswer = PrivatePostAnswer.builder()
                .content(content)
                .build();

        privatePostAnswer.setPrivatePost(privatePost);
        return privatePostAnswer;
    }
}
