package com.example.sabujak.branch.service;
import com.example.sabujak.branch.dto.response.BranchResponseDto;
import com.example.sabujak.branch.exception.BranchException;
import com.example.sabujak.branch.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.sabujak.branch.exception.BranchErrorCode.ENTITY_NOT_FOUND_BY_NAME;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BranchService {

    private final BranchRepository branchRepository;


    public BranchResponseDto findByBranchName(String branchName) {
        log.info("[BranchService findByBranchName] branchName: {}", branchName);
        return branchRepository.findByBranchName(branchName)
                .map(BranchResponseDto::fromEntity).orElseThrow(() -> new BranchException(ENTITY_NOT_FOUND_BY_NAME));
    }

    public List<BranchResponseDto> findAll() {
        log.info("[BranchService findAll]");
        return branchRepository.findAll()
                .stream().map(BranchResponseDto::fromEntity).toList();
    }


}
