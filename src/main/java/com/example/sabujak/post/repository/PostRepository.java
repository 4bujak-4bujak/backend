package com.example.sabujak.post.repository;

import com.example.sabujak.post.entity.Category;
import com.example.sabujak.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
                    "left join fetch p.images i " +
                    "where p.id = :postId"
    )
    Optional<Post> findWithMemberAndImagesById(Long postId);

    @Query(
            value = "select p " +
                    "from Post p " +
                    "join fetch p.member m " +
                    "left join fetch p.images i " +
                    "where p.category = :category and " +
                    "(:cursorId is null or p.id < :cursorId)"
    )
    List<Post> findAllWithMembersAndImagesByCategoryAndId(
            @Param("category") Category category,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query(
            value = "select p " +
                    "from Post p " +
                    "join fetch p.member m " +
                    "left join fetch p.images i " +
                    "where p.category in :categories and " +
                    "(:cursorId is null or p.id < :cursorId)"
    )
    List<Post> findAllWithMembersAndImagesByCategoriesAndId(
            @Param("categories") List<Category> categories,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
