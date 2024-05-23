package com.example.sabujak.post.repository;

import com.example.sabujak.post.entity.Category;
import com.example.sabujak.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(
            value = "select p " +
                    "from Post p " +
                    "join fetch p.member m " +
                    "where p.id = :postId"
    )
    Optional<Post> findWithMemberById(@Param("postId") Long postId);

    @Query(
            value = "select p " +
                    "from Post p " +
                    "join fetch p.member m " +
                    "where (:category is null or p.category = :category) and " +
                    "(:cursorId is null or p.id < :cursorId)"
    )
    List<Post> findAllWithMembersByCategoryAndId(
            @Param("category") Category category,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Lock(PESSIMISTIC_WRITE)
    @Query(
            value = "select p " +
                    "from Post p " +
                    "where p.id = :postId"
    )
    Optional<Post> findByIdWithPessimisticLock(Long postId);
}
