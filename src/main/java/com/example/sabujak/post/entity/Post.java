package com.example.sabujak.post.entity;

import com.example.sabujak.common.entity.BaseEntity;
import com.example.sabujak.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@ToString
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Enumerated(STRING)
    private Category category;
    @Enumerated(STRING)
    private Tag tag;

    private String title;
    private String content;

    private int viewCount = 0;
    private int likeCount = 0;
    private int commentCount = 0;

    @OneToMany(
            mappedBy = "post", fetch = LAZY,
            cascade = ALL, orphanRemoval = true
    )
    private List<Comment> comments;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private Post(
            Category category,
            Tag tag,
            String title,
            String content
    ) {
        this.category = category;
        this.tag = tag;
        this.title = title;
        this.content = content;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }
}
