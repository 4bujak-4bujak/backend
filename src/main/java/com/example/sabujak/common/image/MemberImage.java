package com.example.sabujak.common.image;

import com.example.sabujak.member.entity.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;

@Getter
@Entity
@DiscriminatorValue("M")
public class MemberImage extends Image{

    @OneToOne(mappedBy = "image")
    private Member member;
}
