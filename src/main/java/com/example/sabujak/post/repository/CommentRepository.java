package com.example.sabujak.post.repository;

import com.example.sabujak.post.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(
            value = "select c " +
                    "from Comment c " +
                    "join fetch c.member m " +
                    "join fetch c.post p " +
                    "where c.post.id = :postId and c.id = :commentId"
    )
    Optional<Comment> findWithMemberAndPostByPostIdAndCommentId(
            @Param("postId") Long postId,
            @Param("commentId") Long commentId
    );

    @Query(
            value = "select c " +
                    "from Comment c " +
                    "join fetch c.member m " +
                    "where c.post.id = :postId and " +
                    "(:cursorId is null or c.id < :cursorId)"
    )
    List<Comment> findWithMemberByPostIdAndCursorId(
            @Param("postId") Long postId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
