package com.example.backend.service;

import com.example.backend.dto.request.ReviewRequest;
import com.example.backend.entity.User;

public interface IReviewService {
    String createReview(User user, ReviewRequest request);
}
