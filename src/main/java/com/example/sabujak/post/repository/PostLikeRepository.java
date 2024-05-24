package com.example.sabujak.post.repository;

import com.example.sabujak.post.entity.PostLike;
import com.example.sabujak.post.entity.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
}
