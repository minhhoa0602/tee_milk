package com.example.backend.dto.response;

import com.example.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private User userName;
    private Integer ratingStar;
    private String comment;
    private LocalDateTime createdAt;
    private String imageUrl;
}
