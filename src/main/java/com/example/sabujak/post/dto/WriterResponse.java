package com.example.sabujak.post.dto;

import com.example.sabujak.member.entity.Job;
import com.example.sabujak.member.entity.Member;

public record WriterResponse(
        Job job,
        String nickname
) {
    public static WriterResponse of(Member member){
        return new WriterResponse(
                member.getMemberJob(),
                member.getMemberNickname()
        );
    }
}
