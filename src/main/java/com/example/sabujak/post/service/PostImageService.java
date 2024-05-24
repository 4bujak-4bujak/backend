package com.example.sabujak.post.service;

import com.example.sabujak.image.service.ImageService;
import com.example.sabujak.post.entity.Post;
import com.example.sabujak.post.entity.PostImage;
import com.example.sabujak.post.repository.PostImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostImageService {

    private final ImageService imageService;
    private final PostImageRepository postImageRepository;

    public void saveImages(Post post, MultipartFile[] imageFiles) {
        for(MultipartFile imageFile : imageFiles) {
            String path = imageService.saveImage(imageFile);

            PostImage image = PostImage.builder()
                    .path(path)
                    .build();
            image.setPost(post);
            postImageRepository.save(image);
            log.info("Saved Post Image. Post Image Path: [{}]", path);
        }
    }
}
