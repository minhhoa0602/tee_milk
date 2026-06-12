package com.example.backend.controller;

import com.example.backend.common.BaseResponse;
import com.example.backend.dto.response.CategoryResponse;
import com.example.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    // GET /api/categories
    // Trả về danh sách category đang active để hiển thị tabs trên Android
    @GetMapping
    public ResponseEntity<BaseResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> data = categoryRepository.findByIsActiveTrue()
                .stream()
                .map(cat -> CategoryResponse.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new BaseResponse<>(data, "successfully"));
    }
}