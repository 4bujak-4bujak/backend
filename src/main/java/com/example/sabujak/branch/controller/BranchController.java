package com.example.sabujak.branch.controller;

import com.example.sabujak.branch.dto.response.BranchResponseDto;
import com.example.sabujak.branch.service.BranchService;
import com.example.sabujak.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/branches")
@RestController
public class BranchController {

    private final BranchService branchService;


    @GetMapping("/{name}")
    public ResponseEntity<Response<BranchResponseDto>> getBranchByName(@PathVariable String name) {
        return ResponseEntity.ok(Response.success(branchService.findByBranchName(name)));
    }

    @GetMapping
    public ResponseEntity<Response<List<BranchResponseDto>>> getAllBranches() {
        return ResponseEntity.ok(Response.success(branchService.findAll()));
    }


}
