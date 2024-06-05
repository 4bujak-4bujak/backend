package com.example.sabujak.privatepost.repository;

import com.example.sabujak.privatepost.entity.PrivatePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivatePostRepository extends JpaRepository<PrivatePost, Long> ,PrivatePostRepositoryCustom{
}
