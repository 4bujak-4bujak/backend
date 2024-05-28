package com.example.sabujak.member.entity;

import com.example.sabujak.image.entity.Image;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@DiscriminatorValue("M")
@NoArgsConstructor(access = PROTECTED)
public class MemberImage extends Image {
    @Builder
    private MemberImage(String imageUrl) {
        super(imageUrl);
    }

    @Setter
    @OneToOne(mappedBy = "image", fetch = LAZY)
    private Member member;

    public static MemberImage createDefaultMemberImage() {
        return new MemberImage("https://bzbz-file-bucket.s3.ap-northeast-2.amazonaws.com/Member-Default-Image.png");
    }

}
