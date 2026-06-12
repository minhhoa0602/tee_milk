package com.example.backend.mapper;

import com.example.backend.dto.response.ReviewResponse;
import com.example.backend.entity.Review;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    List<ReviewResponse> reviewsToReviewResponseList(List<Review> reviews);
}
