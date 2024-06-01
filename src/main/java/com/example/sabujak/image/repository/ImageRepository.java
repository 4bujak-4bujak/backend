package com.example.sabujak.image.repository;

import com.example.sabujak.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    void deleteByImageUrl(String imageUrl);
    Optional<Image> findByImageUrl(String imageUrl);
}
