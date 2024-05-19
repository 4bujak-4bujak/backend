package com.example.sabujak.comment.repository;

import com.example.sabujak.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(
            value = "select c " +
                    "from Comment c " +
                    "join fetch c.member m " +
                    "join fetch c.post p " +
                    "where c.post.id = :postId and c.id = :commentId")
    Optional<Comment> findWithMemberAndPostByPostIdAndCommentId(
            @Param("postId") Long postId,
            @Param("commentId") Long commentId
    );
}
