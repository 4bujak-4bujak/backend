package com.example.sabujak.privatepost.dto.request;

import com.example.sabujak.branch.entity.Branch;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.privatepost.entity.PrivatePost;

public record SavePrivatePostRequestDto(String title, String content, String branchName) {

    public PrivatePost toEntity(Member member, Branch branch) {
        PrivatePost privatePost = PrivatePost.builder()
                .title(title)
                .content(content)
                .build();
        privatePost.setMember(member);
        privatePost.setBranch(branch);

        return privatePost;
    }
}
