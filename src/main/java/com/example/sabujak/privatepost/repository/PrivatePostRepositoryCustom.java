package com.example.sabujak.privatepost.repository;

import com.example.sabujak.privatepost.entity.PrivatePost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PrivatePostRepositoryCustom {

    Slice<PrivatePost> findBySlice(Long lastPrivatePostId, Pageable pageable, String memberEmail);
}
