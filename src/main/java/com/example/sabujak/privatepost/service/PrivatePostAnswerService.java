package com.example.sabujak.privatepost.service;

import com.example.sabujak.privatepost.dto.request.SavePrivatePostAnswerRequestDto;
import com.example.sabujak.privatepost.dto.response.PrivatePostAnswerResponseDto;
import com.example.sabujak.privatepost.dto.response.SavePrivatePostAnswerResponseDto;
import com.example.sabujak.privatepost.entity.PrivatePost;
import com.example.sabujak.privatepost.entity.PrivatePostAnswer;
import com.example.sabujak.privatepost.exception.PrivatePostErrorCode;
import com.example.sabujak.privatepost.exception.PrivatePostException;
import com.example.sabujak.privatepost.repository.PrivatePostAnswerRepository;
import com.example.sabujak.privatepost.repository.PrivatePostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PrivatePostAnswerService {
    private final PrivatePostAnswerRepository privatePostAnswerRepository;
    private final PrivatePostRepository privatePostRepository;


    @Transactional
    public SavePrivatePostAnswerResponseDto savePrivatePostAnswer(SavePrivatePostAnswerRequestDto savePrivatePostAnswerRequestDto, Long privatePostId) {
        PrivatePost privatePost = privatePostRepository.findById(privatePostId)
                .orElseThrow(() -> new PrivatePostException(PrivatePostErrorCode.PRIVATEPOST_NOT_FOUND));

        PrivatePostAnswer privatePostAnswer = savePrivatePostAnswerRequestDto.toEntity(privatePost);
        privatePostAnswerRepository.save(privatePostAnswer);
        privatePost.setPrivatePostAnswer(privatePostAnswer);

        log.info("[PrivatePostAnswerService savePrivatePostAnswer] privatePostId: {}, privatePostAnswerId: {}", privatePost.getId(), privatePostAnswer.getId());
        return SavePrivatePostAnswerResponseDto.of(privatePostAnswer);
    }

    public PrivatePostAnswerResponseDto getPrivatePostAnswer(Long privatePostId) {
        PrivatePost privatePost = privatePostRepository.findById(privatePostId)
                .orElseThrow(() -> new PrivatePostException(PrivatePostErrorCode.PRIVATEPOST_NOT_FOUND));

        PrivatePostAnswer privatePostAnswer = privatePost.getPrivatePostAnswer();
        if (Objects.isNull(privatePostAnswer)){
            return null;
        }

        return PrivatePostAnswerResponseDto.of(privatePost.getPrivatePostAnswer());
    }

}
