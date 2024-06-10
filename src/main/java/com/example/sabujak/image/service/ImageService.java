package com.example.sabujak.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.sabujak.image.entity.*;
import com.example.sabujak.image.exception.ImageException;
import com.example.sabujak.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

import static com.example.sabujak.image.exception.ImageErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    private final ImageRepository imageRepository;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final List<String> allowExtensionList = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".svg");

    public String saveImage(MultipartFile imageFile) {
        // 이미지 가 빈 파일 일 경우
        if (imageFile.isEmpty() || Objects.isNull(imageFile)) {
            log.error("[ImageService saveImage] empty image file. imageName: {}", imageFile.getOriginalFilename());
            throw new ImageException(EMPTY_IMAGE_FILE);
        }
        this.validateImageFileExtension(imageFile.getOriginalFilename());

        String originName = imageFile.getOriginalFilename();
        String ext = originName.substring(originName.lastIndexOf(".")); //확장자
        String uploadingName = UUID.randomUUID().toString().substring(0, 10) + originName;// 변경된 파일 명
        return uploadImage(imageFile, ext, uploadingName);
    }

    private String uploadImage(MultipartFile image,
                               String ext,
                               String uploadingName)  {
        ObjectMetadata metadata = new ObjectMetadata();

        if (ext.equals(".svg"))
            metadata.setContentType("image/svg+xml");

        else
            metadata.setContentType("image/" + ext.substring(1));



        try {
            amazonS3.putObject(new PutObjectRequest(
                    bucketName, uploadingName, image.getInputStream(),metadata
            ));
        } catch (IOException e){
            log.error("[ImageService uploadImage] uploadFailed  fileName: {} error Message: {}"
                    ,image.getOriginalFilename(), e.getMessage());
            throw new ImageException(FAILED_UPLOADING_IMAGE_FILE);
        }
        return amazonS3.getUrl(bucketName, uploadingName).toString(); // 이미지 url

    }

    private void validateImageFileExtension(String fileName) {
        int extensionIndex = fileName.lastIndexOf("."); // 확장자

        if (extensionIndex == -1) {
            log.error("[ImageService validateImageFileExtension] image extension not found. imageName: {}", fileName);
            throw new ImageException(NO_EXTENSION_IMAGE_FILE);
        }
        String extension = fileName.substring(extensionIndex);

        if (!allowExtensionList.contains(extension)) {
            log.error("[ImageService validateImageFileExtension] image extension in invalid. imageName: {}, exetention:{}", fileName, extension);
            throw new ImageException(INVALID_EXTENSION_IMAGE_FILE);
        }
    }

    @Transactional
    public void deleteImage(String imageUrl) {
        String key = getKeyFromImageUrl(imageUrl);
        try{
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        }catch (Exception e){
            log.error("[ImageService deleteImage] deleteFailed  imageUrl: {} error Message: {}", imageUrl, e.getMessage());
            throw new ImageException(FAILED_DELETING_IMAGE_FILE);
        }
        imageRepository.deleteByImageUrl(imageUrl);
    }

    private String getKeyFromImageUrl(String imageUrl) {
        try{
            URL url = new URL(imageUrl);
            String decodedKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodedKey.substring(1); // 맨 앞에 '/' 가 있어서 제거하기 위해
        }catch (MalformedURLException | UnsupportedEncodingException e){
            log.error("[ImageService getKeyFromImageUrl] imageUrl: {} error Message: {}", imageUrl, e.getMessage());
            throw new ImageException(FAILED_DELETING_IMAGE_FILE);
        }
    }

    public Image findImageByUrl(String imageUrl) {
        log.info("[ImageService findImageByUrl] imageUrl: {}", imageUrl);
        return imageRepository.findByImageUrl(imageUrl).orElseThrow();
    }
}
