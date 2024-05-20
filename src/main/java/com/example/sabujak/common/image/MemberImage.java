package com.example.sabujak.common.image;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@DiscriminatorValue("M")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberImage extends Image {
    private MemberImage(String imageUrl) {
        super(imageUrl);
    }

    public static MemberImage createDefaultMemberImage() {
        return new MemberImage("https://bzbz-file-bucket.s3.ap-northeast-2.amazonaws.com/Member-Default-Image.png");
    }
}
