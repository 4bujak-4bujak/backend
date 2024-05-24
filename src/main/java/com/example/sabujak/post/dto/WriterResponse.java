package com.example.sabujak.post.dto;

import com.example.sabujak.member.entity.Job;
import com.example.sabujak.member.entity.Member;

public record WriterResponse(
        String profile,
        Job job,
        String nickname
) {
    public static WriterResponse of(Member member){
        return new WriterResponse(
                member.getImage().getImageUrl(),
                member.getMemberJob(),
                member.getMemberNickname()
        );
    }
}
